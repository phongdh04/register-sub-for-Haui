package com.example.demo.domain.enums;

/**
 * Kết quả 1 lần xử lý request đăng ký, lưu trong {@code registration_request_log}.
 */
public enum RegistrationOutcome {
    /** Đăng ký thành công — đã insert {@code DangKyHocPhan}. */
    SUCCESS,
    /** Sinh viên đã đăng ký lớp này hoặc đăng ký trùng. */
    DUPLICATE,
    /** Lớp đã hết slot (atomic guard tại DB). */
    FULL,
    /** Vi phạm validation chain (trùng lịch, thiếu tiên quyết, lớp không công bố...). */
    VALIDATION_FAILED,
    /** Đăng ký ngoài cửa sổ thời gian / lớp không tồn tại / sinh viên không tồn tại. */
    REJECTED,
    /** Hủy đăng ký thành công. */
    CANCELLED
}
