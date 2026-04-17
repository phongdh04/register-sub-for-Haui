package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Task 22 – Thử thách OTP sau bước mật khẩu (chỉ tài khoản ADMIN bật MFA).
 */
@Entity
@Table(name = "mfa_otp_challenge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MfaOtpChallenge {

    @Id
    @Column(name = "id", length = 40, nullable = false)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Column(name = "het_han", nullable = false)
    private Instant expiresAt;

    @Column(name = "da_dung", nullable = false)
    private boolean consumed;
}
