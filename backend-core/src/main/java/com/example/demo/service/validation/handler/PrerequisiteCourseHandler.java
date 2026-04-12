package com.example.demo.service.validation.handler;

import com.example.demo.domain.entity.HocPhan;
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
 * HANDLER 3 (CUỐI CHAIN) – Kiểm tra điều kiện tiên quyết.
 *
 * Đọc field "dieu_kien_rang_buoc_json" của HocPhan (JSONB):
 * Format: {"tien_quyet": ["CS101", "MATH201"], "song_hanh": [], "thay_the": [], "tuong_duong": []}
 *
 * SRP: Chỉ kiểm tra môn tiên quyết, không đụng logic lịch hay slot.
 * OCP: Cấu trúc JSON ràng buộc thay đổi → sửa đọc JSON, không cần sửa chain.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrerequisiteCourseHandler extends AbstractValidationHandler {

    private final LopHocPhanRepository lopHocPhanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @SuppressWarnings("unchecked")
    @Override
    protected void doValidate(RegistrationMessageDto msg) throws RegistrationValidationException {
        LopHocPhan lop = lopHocPhanRepository.findById(msg.getIdLopHp())
                .orElseThrow(() -> new RegistrationValidationException(
                        RegistrationValidationException.LOP_KHONG_TON_TAI,
                        "Lớp không tồn tại khi kiểm tra tiên quyết"));

        HocPhan hocPhan = lop.getHocPhan();
        Map<String, Object> dieuKienJson = hocPhan.getDieuKienRangBuocJson();

        // Học phần chưa có điều kiện ràng buộc → bỏ qua
        if (dieuKienJson == null || dieuKienJson.isEmpty()) {
            log.debug("[TraceID={}] Học phần {} không có điều kiện tiên quyết.",
                    msg.getTraceId(), hocPhan.getMaHocPhan());
            return;
        }

        // Đọc danh sách mã môn tiên quyết từ JSON
        Object tienQuyetRaw = dieuKienJson.get("tien_quyet");
        if (!(tienQuyetRaw instanceof List<?> tienQuyetList) || tienQuyetList.isEmpty()) {
            return; // Không có môn tiên quyết
        }

        List<String> maTienQuyets = (List<String>) tienQuyetList;

        // Lấy danh sách môn SV đã hoàn thành (trạng thái THANH_CONG)
        List<String> daHoanThanh = dangKyHocPhanRepository
                .findCompletedCourseCodes(msg.getIdSinhVien());

        // Tìm môn tiên quyết chưa hoàn thành
        List<String> conThieu = maTienQuyets.stream()
                .filter(ma -> !daHoanThanh.contains(ma))
                .toList();

        if (!conThieu.isEmpty()) {
            throw new RegistrationValidationException(
                    RegistrationValidationException.CHUA_HOC_TIEN_QUYET,
                    String.format("[TraceID=%s] SV ID=%d chưa hoàn thành môn tiên quyết %s của học phần %s.",
                            msg.getTraceId(), msg.getIdSinhVien(),
                            conThieu, hocPhan.getMaHocPhan()));
        }

        log.debug("[TraceID={}] Kiểm tra tiên quyết OK: SV={} HP={}",
                msg.getTraceId(), msg.getIdSinhVien(), hocPhan.getMaHocPhan());
    }
}
