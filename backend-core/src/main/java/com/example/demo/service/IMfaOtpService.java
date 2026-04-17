package com.example.demo.service;

import com.example.demo.domain.entity.User;

public interface IMfaOtpService {

    /** Tạo thử thách OTP, gửi (hoặc ghi log demo) mã, trả id thử thách. */
    String createChallenge(User user);

    /** Kiểm tra OTP, đánh dấu đã dùng, trả user tương ứng. */
    User validateAndConsume(String challengeId, String rawOtp);
}
