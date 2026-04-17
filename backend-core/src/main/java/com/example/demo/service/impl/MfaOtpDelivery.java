package com.example.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Gửi OTP: môi trường demo ghi log (xem console backend). Có thể thay bằng JavaMail sau.
 */
@Component
@Slf4j
public class MfaOtpDelivery {

    public void sendLoginOtp(String toEmail, String username, String otp) {
        log.warn(
                "[MFA Task 22] OTP đăng nhập cho admin '{}' — email đích: {} — mã: {} (chưa cấu hình SMTP: kiểm tra log này khi demo)",
                username,
                toEmail,
                otp);
    }
}
