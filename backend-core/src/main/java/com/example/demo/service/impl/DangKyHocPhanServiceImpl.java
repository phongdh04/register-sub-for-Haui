package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service điều phối toàn bộ luồng xử lý đăng ký học phần sau khi nhận message từ Kafka.
 *
 * Luồng: Kafka Message → Build Chain → Validate → INCREMENT siSoThucTe → INSERT DangKyHocPhan
 *
 * SRP: Chỉ điều phối chain và ghi DB, không tự validate.
 * DIP: Phụ thuộc vào interface IDangKyValidationHandler, không phụ thuộc handler cụ thể.
 * OCP: Thêm rule mới → thêm Handler mới và lắp vào chain ở buildValidationChain().
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DangKyHocPhanServiceImpl {

    // ── Repositories ──────────────────────────────────────────
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LopHocPhanRepository    lopHocPhanRepository;
    private final HocKyRepository         hocKyRepository;
    private final SinhVienRepository      sinhVienRepository;

    // ── Handlers (inject bởi Spring, khai báo là Bean @Component) ──
    private final DuplicateRegistrationHandler  duplicateRegistrationHandler;
    private final ScheduleConflictHandler       scheduleConflictHandler;
    private final PrerequisiteCourseHandler     prerequisiteCourseHandler;
    private final RegistrationScheduleChecker   registrationScheduleChecker;

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
        log.info("🔄 Bắt đầu xử lý ĐKHP: traceID={} svID={} lopID={}",
                msg.getTraceId(), msg.getIdSinhVien(), msg.getIdLopHp());

        HocKy hocKyGate = hocKyRepository.findById(msg.getIdHocKy()).orElse(null);
        if (hocKyGate == null) {
            log.warn("⚠️ Từ chối ĐKHP: không tìm thấy học kỳ {} (traceID={})", msg.getIdHocKy(), msg.getTraceId());
            return false;
        }
        if (!registrationScheduleChecker.isOfficialRegistrationOpen(hocKyGate)) {
            log.warn("⚠️ Từ chối ĐKHP: ngoài phiên đăng ký chính thức (hocKy={} traceID={})",
                    msg.getIdHocKy(), msg.getTraceId());
            return false;
        }

        // ── Bước 1: Chạy toàn bộ Chain of Responsibility ──────
        try {
            validationChain.validate(msg);
        } catch (RegistrationValidationException e) {
            log.warn("⚠️ Validation thất bại [{}]: {}", e.getErrorCode(), e.getMessage());
            return false;
        }

        // ── Bước 2: Nạp các entity cần thiết ─────────────────
        SinhVien sinhVien = sinhVienRepository.findById(msg.getIdSinhVien())
                .orElseThrow(() -> new EntityNotFoundException(
                        "SinhVien không tồn tại: " + msg.getIdSinhVien()));

        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(msg.getIdLopHp())
                .orElseThrow(() -> new EntityNotFoundException(
                        "LopHocPhan không tồn tại: " + msg.getIdLopHp()));

        HocKy hocKy = hocKyRepository.findById(msg.getIdHocKy())
                .orElseThrow(() -> new EntityNotFoundException(
                        "HocKy không tồn tại: " + msg.getIdHocKy()));

        // ── Bước 3: INCREMENT sĩ số thực tế (atomic UPDATE) ──
        int updated = lopHocPhanRepository.incrementSiSoThucTe(lopHocPhan.getIdLopHp());
        if (updated == 0) {
            // incrementSiSoThucTe trả về 0 khi siSoThucTe >= siSoToiDa (WHERE guard)
            log.warn("⚠️ Hết chỗ tại DB cho lớp {} (traceID={})",
                    lopHocPhan.getMaLopHp(), msg.getTraceId());
            return false;
        }

        // ── Bước 4: INSERT bản ghi đăng ký vào DB ────────────
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
        return true;
    }

    /**
     * Xử lý yêu cầu hủy đăng ký học phần từ Kafka (traceID bắt đầu bằng "CANCEL-").
     * Cập nhật trạng thái → RUT_MON và DECREMENT sĩ số thực tế.
     */
    @Transactional
    public void processCancellation(RegistrationMessageDto msg) {
        log.info("🗑️ Bắt đầu xử lý HỦY ĐKHP: traceID={} svID={} lopID={}",
                msg.getTraceId(), msg.getIdSinhVien(), msg.getIdLopHp());

        // Tìm bản ghi đăng ký (nếu có)
        dangKyHocPhanRepository
                .findRegisteredCoursesInSemester(msg.getIdSinhVien(), msg.getIdHocKy())
                .stream()
                .filter(d -> d.getLopHocPhan().getIdLopHp().equals(msg.getIdLopHp()))
                .findFirst()
                .ifPresentOrElse(dkhp -> {
                    dkhp.setTrangThaiDangKy("RUT_MON");
                    dangKyHocPhanRepository.save(dkhp);
                    // DECREMENT sĩ số thực tế
                    lopHocPhanRepository.decrementSiSoThucTe(msg.getIdLopHp());
                    log.info("✅ Hủy ĐKHP thành công: traceID={} idDangKy={}",
                            msg.getTraceId(), dkhp.getIdDangKy());
                }, () -> log.warn("⚠️ Không tìm thấy đăng ký để hủy: svID={} lopID={}",
                        msg.getIdSinhVien(), msg.getIdLopHp()));
    }
}
