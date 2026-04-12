package com.example.demo.service.validation.handler;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.validation.AbstractValidationHandler;
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
                    if (isConflict(slotMoi, slotCu)) {
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

    /**
     * So sánh 2 slot TKB có trùng nhau không.
     * Format JSONB: {"thu": 2, "tiet": "1-3", "phong": "A.101", ...}
     * Trùng khi: cùng thứ VÀ tiết học giao nhau.
     */
    private boolean isConflict(Map<String, Object> slotA, Map<String, Object> slotB) {
        Object thuA = slotA.get("thu");
        Object thuB = slotB.get("thu");

        // Khác thứ → không thể trùng
        if (thuA == null || !thuA.equals(thuB)) return false;

        String tietA = String.valueOf(slotA.getOrDefault("tiet", ""));
        String tietB = String.valueOf(slotB.getOrDefault("tiet", ""));

        return isTietOverlap(tietA, tietB);
    }

    /**
     * Kiểm tra 2 khoảng tiết giao nhau không.
     * Format: "1-3" nghĩa là tiết 1, 2, 3.
     * "7" nghĩa là tiết 7.
     */
    private boolean isTietOverlap(String tietA, String tietB) {
        try {
            int[] rangeA = parseTiet(tietA);
            int[] rangeB = parseTiet(tietB);
            // Hai khoảng [a1, a2] và [b1, b2] giao nhau khi a1 <= b2 VÀ b1 <= a2
            return rangeA[0] <= rangeB[1] && rangeB[0] <= rangeA[1];
        } catch (Exception e) {
            log.warn("Không thể parse tiết học: '{}' vs '{}', bỏ qua kiểm tra.", tietA, tietB);
            return false;
        }
    }

    /** Parse "1-3" → [1, 3] hoặc "7" → [7, 7] */
    private int[] parseTiet(String tiet) {
        if (tiet.contains("-")) {
            String[] parts = tiet.split("-");
            return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
        }
        int t = Integer.parseInt(tiet.trim());
        return new int[]{t, t};
    }
}
