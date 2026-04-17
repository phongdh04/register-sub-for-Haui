package com.example.demo.service.validation.handler;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.validation.AbstractValidationHandler;
import com.example.demo.util.TkbSlotConflictUtils;
import com.example.demo.service.validation.RegistrationValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * HANDLER 2 – Kiểm tra lịch học của lớp mới có bị trùng với các lớp đã đăng ký không.
 *
 * Thuật toán: So sánh JSONB thoiKhoaBieuJson của lớp đăng ký mới
 *             với JSONB của từng lớp SV đã đăng ký trong cùng HK.
 *
 * SRP: Chỉ kiểm tra conflict lịch học (thu + tiet), không xử lý logic khác.
 * OCP: Cấu trúc JSONB thay đổi chỉ cần sửa method parseSlots(), không đụng chain.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleConflictHandler extends AbstractValidationHandler {

    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    @Override
    protected void doValidate(RegistrationMessageDto msg) throws RegistrationValidationException {
        // Lấy lớp mới muốn đăng ký để lấy TKB
        LopHocPhan lopMoi = lopHocPhanRepository.findById(msg.getIdLopHp())
                .orElseThrow(() -> new RegistrationValidationException(
                        RegistrationValidationException.LOP_KHONG_TON_TAI,
                        "Lớp không tồn tại khi kiểm tra lịch"));

        List<Map<String, Object>> tkbMoi = lopMoi.getThoiKhoaBieuJson();
        if (tkbMoi == null || tkbMoi.isEmpty()) {
            // Lớp chưa có TKB → bỏ qua kiểm tra (không nên block đăng ký)
            log.warn("[TraceID={}] Lớp {} chưa có TKB JSON, bỏ qua kiểm tra trùng lịch.",
                    msg.getTraceId(), lopMoi.getMaLopHp());
            return;
        }

        // Lấy tất cả lớp SV đã đăng ký trong HK
        List<DangKyHocPhan> daDangKy = dangKyHocPhanRepository
                .findRegisteredCoursesInSemester(msg.getIdSinhVien(), msg.getIdHocKy());

        for (DangKyHocPhan dkhp : daDangKy) {
            LopHocPhan lopCu = dkhp.getLopHocPhan();
            List<Map<String, Object>> tkbCu = lopCu.getThoiKhoaBieuJson();
            if (tkbCu == null || tkbCu.isEmpty()) continue;

            // Kiểm tra từng slot của lớp mới có trùng với bất kỳ slot lớp cũ nào
            for (Map<String, Object> slotMoi : tkbMoi) {
                for (Map<String, Object> slotCu : tkbCu) {
                    if (TkbSlotConflictUtils.slotsConflict(slotMoi, slotCu)) {
                        throw new RegistrationValidationException(
                                RegistrationValidationException.TRUNG_LICH,
                                String.format("[TraceID=%s] SV ID=%d: Lớp %s trùng lịch với lớp %s đã đăng ký. " +
                                              "Thứ=%s, Tiết=%s",
                                        msg.getTraceId(), msg.getIdSinhVien(),
                                        lopMoi.getMaLopHp(), lopCu.getMaLopHp(),
                                        slotMoi.get("thu"), slotMoi.get("tiet")));
                    }
                }
            }
        }
    }

}
