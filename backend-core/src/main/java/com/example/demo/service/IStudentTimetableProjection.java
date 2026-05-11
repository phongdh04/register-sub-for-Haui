package com.example.demo.service;

import com.example.demo.domain.entity.StudentTimetableEntry;

import java.util.List;

/**
 * Sprint 5 — Projection service quản lý read-model TKB của sinh viên.
 *
 * <p>Read-model được cập nhật bởi event listener AFTER_COMMIT để tránh ảnh hưởng
 * latency luồng đăng ký, và bảo đảm chỉ ghi sau khi DB đã chốt.
 */
public interface IStudentTimetableProjection {

    /**
     * Cập nhật/khởi tạo entries cho 1 đăng ký vừa được commit thành công.
     * Idempotent: chạy lại với cùng {@code idDangKy} sẽ replace toàn bộ slot cũ
     * (an toàn cho replay event AFTER_COMMIT).
     *
     * @param idDangKy id của {@code DangKyHocPhan} vừa insert
     * @return số slot đã được ghi (0 nếu lớp không có TKB JSON)
     */
    int upsertForRegistration(Long idDangKy);

    /**
     * Xoá toàn bộ entries thuộc 1 đăng ký (được gọi khi sinh viên hủy/rút môn).
     */
    int removeForRegistration(Long idDangKy);

    /**
     * Rebuild toàn bộ TKB của 1 sinh viên trong 1 học kỳ — dùng cho admin tool / data repair.
     */
    int rebuildForStudent(Long sinhVienId, Long hocKyId);

    /**
     * Read read-model — phục vụ TimetableController nhanh.
     */
    List<StudentTimetableEntry> readForStudent(Long sinhVienId, Long hocKyId);
}
