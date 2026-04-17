package com.example.demo.service.impl;

import com.example.demo.domain.entity.MfaOtpChallenge;
import com.example.demo.domain.entity.User;
import com.example.demo.repository.MfaOtpChallengeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IMfaOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class MfaOtpServiceImpl implements IMfaOtpService {

    private final MfaOtpChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaOtpDelivery mfaOtpDelivery;

    @Value("${eduport.mfa.otp-ttl-minutes:5}")
    private int otpTtlMinutes;

    @Override
    @Transactional
    public String createChallenge(User user) {
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        String id = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(otpTtlMinutes, ChronoUnit.MINUTES);
        MfaOtpChallenge challenge = MfaOtpChallenge.builder()
                .id(id)
                .username(user.getUsername())
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(expiresAt)
                .consumed(false)
                .build();
        challengeRepository.save(challenge);
        mfaOtpDelivery.sendLoginOtp(user.getEmail(), user.getUsername(), otp);
        return id;
    }

    @Override
    @Transactional
    public User validateAndConsume(String challengeId, String rawOtp) {
        if (rawOtp == null || rawOtp.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Thiếu mã OTP.");
        }
        MfaOtpChallenge ch = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Phiên xác thực không hợp lệ."));
        if (ch.isConsumed()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Mã OTP đã được sử dụng.");
        }
        if (Instant.now().isAfter(ch.getExpiresAt())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Mã OTP đã hết hạn.");
        }
        if (!passwordEncoder.matches(rawOtp.trim(), ch.getOtpHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Mã OTP không đúng.");
        }
        ch.setConsumed(true);
        challengeRepository.save(ch);
        return userRepository.findByUsername(ch.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Không tìm thấy tài khoản."));
    }
}
