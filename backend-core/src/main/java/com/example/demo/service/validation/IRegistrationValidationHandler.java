package com.example.demo.service.validation;

import com.example.demo.payload.request.RegistrationMessageDto;

/**
 * Interface cho Chain of Responsibility Pattern – Validation ĐKHP.
 *
 * Mỗi Handler trong chain chỉ làm đúng 1 việc validate (SRP).
 * Handler có thể dừng chain bằng cách ném ValidationException,
 * hoặc chuyển tiếp cho handler kế tiếp bằng cách gọi next.validate().
 *
 * ISP: Interface nhỏ gọn, không ép implement method không cần thiết.
 * OCP: Thêm rule mới chỉ cần thêm class Handler mới, không sửa code cũ.
 */
public interface IRegistrationValidationHandler {

    /**
     * Đặt handler tiếp theo trong chuỗi.
     * @return chính handler này để hỗ trợ method chaining khi build chain.
     */
    IRegistrationValidationHandler setNext(IRegistrationValidationHandler next);

    /**
     * Thực thi validation. Nếu phát hiện vi phạm → throw ValidationException.
     * Nếu hợp lệ → gọi next.validate() để tiếp tục chain.
     *
     * @param msg Message từ Kafka chứa idSinhVien, idLopHp, idHocKy.
     * @throws DangKyValidationException khi vi phạm rule nghiệp vụ.
     */
    void validate(RegistrationMessageDto msg) throws RegistrationValidationException;
}
