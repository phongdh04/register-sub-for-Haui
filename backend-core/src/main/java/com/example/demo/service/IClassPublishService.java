package com.example.demo.service;

import com.example.demo.payload.request.LopHocPhanAssignGiangVienRequest;
import com.example.demo.payload.response.LopHocPhanBulkPublishResponse;
import com.example.demo.payload.response.LopHocPhanPublishResponse;

/**
 * ISP: Quản lý vòng đời công bố {@code lop_hoc_phan}
 * (Sprint 3 — class publish workflow: SHELL → SCHEDULED → PUBLISHED).
 */
public interface IClassPublishService {

    /**
     * Gán giảng viên cho 1 lớp. Nếu lớp đã có {@code thoiKhoaBieuJson},
     * status_publish được nâng lên SCHEDULED tự động.
     */
    LopHocPhanPublishResponse assignGiangVien(Long idLopHp, LopHocPhanAssignGiangVienRequest request);

    /**
     * Publish 1 lớp (chuyển sang PUBLISHED). Yêu cầu lớp đã có giảng viên + lịch + trạng thái SCHEDULED.
     */
    LopHocPhanPublishResponse publish(Long idLopHp);

    /**
     * Bulk publish toàn bộ lớp đã SCHEDULED của 1 học kỳ. Trả về số lớp đã publish + danh sách bị bỏ qua.
     */
    LopHocPhanBulkPublishResponse bulkPublish(Long idHocKy);

    /**
     * Force-publish: mở tất cả lớp trong học kỳ về PUBLISHED + trang_thai DANG_MO,
     * bỏ qua điều kiện kiểm tra GV/lịch. Dùng cho demo/đồ án để mở đăng ký nhanh.
     */
    LopHocPhanBulkPublishResponse forcePublishAll(Long idHocKy);
}
