package com.example.demo.service.validation;

/**
 * Exception nghiệp vụ khi quá trình validation ĐKHP phát hiện vi phạm.
 * Chứa mã lỗi (errorCode) để Consumer có thể log + phân loại lý do thất bại.
 *
 * Không extends RuntimeException để buộc caller phải xử lý tường minh (checked).
 */
public class RegistrationValidationException extends Exception {

    private final String errorCode;

    public RegistrationValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // ────────────────────────────────────────────────────────
    // Các ErrorCode chuẩn của hệ thống ĐKHP
    // ────────────────────────────────────────────────────────

    /** LopHocPhan không tồn tại hoặc chưa mở */
    public static final String LOP_KHONG_TON_TAI = "LOP_KHONG_TON_TAI";

    /** SinhVien đã đăng ký môn này rồi (duplicate) */
    public static final String TRUNG_LOP = "TRUNG_LOP";

    /** Lịch học bị trùng với môn đã đăng ký khác */
    public static final String TRUNG_LICH = "TRUNG_LICH";

    /** Chưa hoàn thành môn học tiên quyết */
    public static final String CHUA_HOC_TIEN_QUYET = "CHUA_HOC_TIEN_QUYET";

    /** Lớp đã hết chỗ (safety check phía DB) */
    public static final String HET_CHO = "HET_CHO";
}
