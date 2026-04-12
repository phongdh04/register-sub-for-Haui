package com.example.demo.service.validation.handler;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.validation.AbstractValidationHandler;
import com.example.demo.service.validation.RegistrationValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * HANDLER 1 – Kiểm tra lớp học phần tồn tại, đang mở và SV chưa đăng ký.
 *
 * Là handler ĐẦU TIÊN trong chain – fail fast ngay nếu dữ liệu cơ bản sai.
 * SRP: Chỉ kiểm tra 2 điều:
 *   1. LopHocPhan tồn tại và có trạng thái DANG_MO.
 *   2. SV chưa đăng ký lớp này trong học kỳ này.
 */
@Component
@RequiredArgsConstructor
public class DuplicateRegistrationHandler extends AbstractValidationHandler {

    private final LopHocPhanRepository lopHocPhanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @Override
    protected void doValidate(RegistrationMessageDto msg) throws RegistrationValidationException {
        // ── Kiểm tra lớp tồn tại và đang mở ──────────────────
        Optional<LopHocPhan> lopOpt = lopHocPhanRepository.findById(msg.getIdLopHp());
        if (lopOpt.isEmpty()) {
            throw new RegistrationValidationException(
                    RegistrationValidationException.LOP_KHONG_TON_TAI,
                    String.format("[TraceID=%s] Lớp học phần ID=%d không tồn tại.",
                            msg.getTraceId(), msg.getIdLopHp()));
        }

        LopHocPhan lop = lopOpt.get();
        if (!"DANG_MO".equals(lop.getTrangThai())) {
            throw new RegistrationValidationException(
                    RegistrationValidationException.LOP_KHONG_TON_TAI,
                    String.format("[TraceID=%s] Lớp %s đang ở trạng thái %s, không thể đăng ký.",
                            msg.getTraceId(), lop.getMaLopHp(), lop.getTrangThai()));
        }

        // ── Safety check sĩ số phía DB (Redis đã check trước nhưng cần double-check) ──
        if (lop.getSiSoThucTe() != null && lop.getSiSoThucTe() >= lop.getSiSoToiDa()) {
            throw new RegistrationValidationException(
                    RegistrationValidationException.HET_CHO,
                    String.format("[TraceID=%s] Lớp %s đã đầy (DB): %d/%d chỗ.",
                            msg.getTraceId(), lop.getMaLopHp(),
                            lop.getSiSoThucTe(), lop.getSiSoToiDa()));
        }

        // ── Kiểm tra SV chưa đăng ký lớp này ──────────────────
        boolean daDangKy = dangKyHocPhanRepository
                .existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(
                        msg.getIdSinhVien(), msg.getIdLopHp(), msg.getIdHocKy());
        if (daDangKy) {
            throw new RegistrationValidationException(
                    RegistrationValidationException.TRUNG_LOP,
                    String.format("[TraceID=%s] SV ID=%d đã đăng ký lớp ID=%d trong học kỳ ID=%d.",
                            msg.getTraceId(), msg.getIdSinhVien(),
                            msg.getIdLopHp(), msg.getIdHocKy()));
        }
    }
}
