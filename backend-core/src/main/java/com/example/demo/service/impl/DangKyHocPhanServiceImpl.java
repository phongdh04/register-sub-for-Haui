package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.RegistrationRequestLog;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.enums.RegistrationOutcome;
import com.example.demo.event.RegistrationCancelledEvent;
import com.example.demo.event.RegistrationConfirmedEvent;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.service.validation.IRegistrationValidationHandler;
import com.example.demo.service.validation.RegistrationValidationException;
import com.example.demo.service.validation.handler.DuplicateRegistrationHandler;
import com.example.demo.service.validation.handler.PrerequisiteCourseHandler;
import com.example.demo.support.RegistrationScheduleChecker;
import com.example.demo.service.validation.handler.ScheduleConflictHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service điều phối toàn bộ luồng xử lý đăng ký học phần sau khi nhận message từ Kafka.
 *
 * Luồng: Kafka Message → Idempotency Check → Build Chain → Validate
 *        → INCREMENT siSoThucTe → INSERT DangKyHocPhan → Publish event → Write log
 *
 * SRP: Chỉ điều phối chain và ghi DB, không tự validate.
 * DIP: Phụ thuộc vào interface IDangKyValidationHandler, không phụ thuộc handler cụ thể.
 * OCP: Thêm rule mới → thêm Handler mới và lắp vào chain ở buildValidationChain().
 *
 * Sprint 4 — bổ sung idempotency log + event AFTER_COMMIT cho downstream (TKB cache, dashboard).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DangKyHocPhanServiceImpl {

    private static final String REQ_TYPE_REGISTER = "REGISTER";
    private static final String REQ_TYPE_CANCEL = "CANCEL";

    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LopHocPhanRepository    lopHocPhanRepository;
    private final HocKyRepository         hocKyRepository;
    private final SinhVienRepository      sinhVienRepository;

    private final DuplicateRegistrationHandler  duplicateRegistrationHandler;
    private final ScheduleConflictHandler       scheduleConflictHandler;
    private final PrerequisiteCourseHandler     prerequisiteCourseHandler;
    private final RegistrationScheduleChecker   registrationScheduleChecker;

    private final RegistrationIdempotencyService idempotencyService;
    private final ApplicationEventPublisher      eventPublisher;

    /** Chain đã được lắp sẵn, dùng lại cho mọi request (stateless). */
    private IRegistrationValidationHandler validationChain;

    /**
     * Lắp chain sau khi Spring inject xong tất cả handlers.
     * Thứ tự: TrungLop (fail-fast) → TrungLich → TienQuyet (tốn DB nhất)
     * OCP: Thêm handler mới chỉ cần thêm vào cuối setNext() chain ở đây.
     */
    @PostConstruct
    public void buildValidationChain() {
        duplicateRegistrationHandler
                .setNext(scheduleConflictHandler)
                .setNext(prerequisiteCourseHandler);
        validationChain = duplicateRegistrationHandler;
        log.info("✅ Validation Chain đã được lắp: TrungLop → TrungLich → TienQuyet");
    }

    /**
     * Xử lý yêu cầu đăng ký học phần từ Kafka.
     * @param msg DTO deserialized từ Kafka message của Go Service.
     * @return true nếu đăng ký thành công, false nếu vi phạm nghiệp vụ.
     */
    @Transactional
    public boolean processRegistration(RegistrationMessageDto msg) {
        String idempotencyKey = resolveIdempotencyKey(msg);
        log.info("🔄 Bắt đầu xử lý ĐKHP: traceID={} svID={} lopID={}",
                msg.getTraceId(), msg.getIdSinhVien(), msg.getIdLopHp());

        Optional<RegistrationRequestLog> prior = idempotencyService.findByKey(idempotencyKey);
        if (prior.isPresent()) {
            RegistrationRequestLog logRow = prior.get();
            log.info("♻️ Idempotent replay (key={}, outcome={}) — bỏ qua xử lý lại.",
                    idempotencyKey, logRow.getOutcome());
            return logRow.getOutcome() == RegistrationOutcome.SUCCESS;
        }

        HocKy hocKyGate = hocKyRepository.findById(msg.getIdHocKy()).orElse(null);
        if (hocKyGate == null) {
            log.warn("⚠️ Từ chối ĐKHP: không tìm thấy học kỳ {} (traceID={})", msg.getIdHocKy(), msg.getTraceId());
            writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, RegistrationOutcome.REJECTED,
                    null, "HOC_KY_NOT_FOUND", "Học kỳ không tồn tại");
            return false;
        }

        SinhVien gateSinhVien = sinhVienRepository.findById(msg.getIdSinhVien()).orElse(null);
        if (gateSinhVien == null) {
            log.warn("⚠️ Từ chối ĐKHP: không tìm thấy sinh viên {} (traceID={})",
                    msg.getIdSinhVien(), msg.getTraceId());
            writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, RegistrationOutcome.REJECTED,
                    null, "SINH_VIEN_NOT_FOUND", "Sinh viên không tồn tại");
            return false;
        }

        Integer cohortNamNhapHoc = resolveCohortNamNhapHoc(gateSinhVien);
        Long cohortNganhId = resolveCohortNganhId(gateSinhVien);
        if (!registrationScheduleChecker.isOfficialRegistrationOpenFor(hocKyGate, cohortNamNhapHoc, cohortNganhId)) {
            log.warn("⚠️ Từ chối ĐKHP: ngoài phiên đăng ký chính thức (hocKy={} cohort={} nganh={} traceID={})",
                    msg.getIdHocKy(), cohortNamNhapHoc, cohortNganhId, msg.getTraceId());
            writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, RegistrationOutcome.REJECTED,
                    null, "REGISTRATION_WINDOW_CLOSED", "Ngoài phiên đăng ký chính thức");
            return false;
        }

        try {
            validationChain.validate(msg);
        } catch (RegistrationValidationException e) {
            log.warn("⚠️ Validation thất bại [{}]: {}", e.getErrorCode(), e.getMessage());
            RegistrationOutcome outcome = mapValidationOutcome(e.getErrorCode());
            writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, outcome,
                    null, e.getErrorCode(), e.getMessage());
            return false;
        }

        SinhVien sinhVien = gateSinhVien;

        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(msg.getIdLopHp())
                .orElseThrow(() -> new EntityNotFoundException(
                        "LopHocPhan không tồn tại: " + msg.getIdLopHp()));

        HocKy hocKy = hocKyRepository.findById(msg.getIdHocKy())
                .orElseThrow(() -> new EntityNotFoundException(
                        "HocKy không tồn tại: " + msg.getIdHocKy()));

        int updated = lopHocPhanRepository.incrementSiSoThucTe(lopHocPhan.getIdLopHp());
        if (updated == 0) {
            log.warn("⚠️ Hết chỗ tại DB cho lớp {} (traceID={})",
                    lopHocPhan.getMaLopHp(), msg.getTraceId());
            writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, RegistrationOutcome.FULL,
                    null, "LOP_HET_SLOT", "Lớp đã đầy slot");
            return false;
        }

        DangKyHocPhan dkhp = DangKyHocPhan.builder()
                .sinhVien(sinhVien)
                .lopHocPhan(lopHocPhan)
                .hocKy(hocKy)
                .trangThaiDangKy("THANH_CONG")
                .build();
        dangKyHocPhanRepository.save(dkhp);

        log.info("✅ ĐKHP thành công: traceID={} svID={} lopMaHP={} idDangKy={}",
                msg.getTraceId(), msg.getIdSinhVien(),
                lopHocPhan.getMaLopHp(), dkhp.getIdDangKy());

        writeLog(idempotencyKey, msg, REQ_TYPE_REGISTER, RegistrationOutcome.SUCCESS,
                dkhp.getIdDangKy(), null, null);

        eventPublisher.publishEvent(RegistrationConfirmedEvent.builder()
                .idempotencyKey(idempotencyKey)
                .traceId(msg.getTraceId())
                .idDangKy(dkhp.getIdDangKy())
                .idSinhVien(sinhVien.getIdSinhVien())
                .idLopHp(lopHocPhan.getIdLopHp())
                .idHocKy(hocKy.getIdHocKy())
                .occurredAt(Instant.now())
                .build());

        return true;
    }

    /** Lấy cohort namNhapHoc từ Lop hành chính của sinh viên (null-safe). */
    private static Integer resolveCohortNamNhapHoc(SinhVien sv) {
        if (sv == null) {
            return null;
        }
        Lop lop = sv.getLop();
        return lop != null ? lop.getNamNhapHoc() : null;
    }

    /** Lấy idNganh của sinh viên (null-safe). */
    private static Long resolveCohortNganhId(SinhVien sv) {
        if (sv == null || sv.getLop() == null) {
            return null;
        }
        NganhDaoTao nganh = sv.getLop().getNganhDaoTao();
        return nganh != null ? nganh.getIdNganh() : null;
    }

    /**
     * Xử lý yêu cầu hủy đăng ký học phần từ Kafka (traceID bắt đầu bằng "CANCEL-").
     * Cập nhật trạng thái → RUT_MON và DECREMENT sĩ số thực tế.
     */
    @Transactional
    public void processCancellation(RegistrationMessageDto msg) {
        String idempotencyKey = resolveIdempotencyKey(msg);
        log.info("🗑️ Bắt đầu xử lý HỦY ĐKHP: traceID={} svID={} lopID={}",
                msg.getTraceId(), msg.getIdSinhVien(), msg.getIdLopHp());

        Optional<RegistrationRequestLog> prior = idempotencyService.findByKey(idempotencyKey);
        if (prior.isPresent()) {
            log.info("♻️ Idempotent replay HỦY (key={}, outcome={}) — bỏ qua.",
                    idempotencyKey, prior.get().getOutcome());
            return;
        }

        var registrationOpt = dangKyHocPhanRepository
                .findRegisteredCoursesInSemester(msg.getIdSinhVien(), msg.getIdHocKy())
                .stream()
                .filter(d -> d.getLopHocPhan().getIdLopHp().equals(msg.getIdLopHp()))
                .findFirst();

        if (registrationOpt.isEmpty()) {
            log.warn("⚠️ Không tìm thấy đăng ký để hủy: svID={} lopID={}",
                    msg.getIdSinhVien(), msg.getIdLopHp());
            writeLog(idempotencyKey, msg, REQ_TYPE_CANCEL, RegistrationOutcome.REJECTED,
                    null, "DANG_KY_NOT_FOUND", "Không tìm thấy đăng ký để hủy");
            return;
        }

        DangKyHocPhan dkhp = registrationOpt.get();
        dkhp.setTrangThaiDangKy("RUT_MON");
        dangKyHocPhanRepository.save(dkhp);
        lopHocPhanRepository.decrementSiSoThucTe(msg.getIdLopHp());
        log.info("✅ Hủy ĐKHP thành công: traceID={} idDangKy={}",
                msg.getTraceId(), dkhp.getIdDangKy());

        writeLog(idempotencyKey, msg, REQ_TYPE_CANCEL, RegistrationOutcome.CANCELLED,
                dkhp.getIdDangKy(), null, null);

        eventPublisher.publishEvent(RegistrationCancelledEvent.builder()
                .idempotencyKey(idempotencyKey)
                .traceId(msg.getTraceId())
                .idDangKy(dkhp.getIdDangKy())
                .idSinhVien(msg.getIdSinhVien())
                .idLopHp(msg.getIdLopHp())
                .idHocKy(msg.getIdHocKy())
                .occurredAt(Instant.now())
                .build());
    }

    private static String resolveIdempotencyKey(RegistrationMessageDto msg) {
        if (msg.getTraceId() != null && !msg.getTraceId().isBlank()) {
            return msg.getTraceId();
        }
        return String.format("AUTO-%d-%d-%d-%d",
                msg.getIdSinhVien() == null ? 0 : msg.getIdSinhVien(),
                msg.getIdLopHp() == null ? 0 : msg.getIdLopHp(),
                msg.getIdHocKy() == null ? 0 : msg.getIdHocKy(),
                System.nanoTime());
    }

    private void writeLog(String idempotencyKey,
                          RegistrationMessageDto msg,
                          String requestType,
                          RegistrationOutcome outcome,
                          Long idDangKy,
                          String errorCode,
                          String errorMessage) {
        idempotencyService.writeLog(idempotencyKey,
                msg.getIdSinhVien(),
                msg.getIdLopHp(),
                msg.getIdHocKy(),
                requestType,
                outcome,
                idDangKy,
                errorCode,
                errorMessage);
    }

    /** Map mã lỗi validation → outcome enum. */
    private static RegistrationOutcome mapValidationOutcome(String errorCode) {
        if (errorCode == null) {
            return RegistrationOutcome.VALIDATION_FAILED;
        }
        return switch (errorCode) {
            case RegistrationValidationException.TRUNG_LOP -> RegistrationOutcome.DUPLICATE;
            default -> RegistrationOutcome.VALIDATION_FAILED;
        };
    }
}
