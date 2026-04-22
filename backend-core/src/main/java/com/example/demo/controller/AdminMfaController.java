package com.example.demo.controller;

import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.AdminMfaUpdateRequest;
import com.example.demo.payload.response.AdminMfaStatusResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.UserDetailsImpl;
import com.example.demo.util.EmailMaskUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Task 22 – Cấu hình MFA (email OTP) cho tài khoản admin đang đăng nhập.
 */
@RestController
@RequestMapping("/api/v1/admin/mfa")
@RequiredArgsConstructor
public class AdminMfaController {

    private final UserRepository userRepository;

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminMfaStatusResponse> status() {
        User user = currentAdmin();
        boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();
        return ResponseEntity.ok(new AdminMfaStatusResponse(
                Boolean.TRUE.equals(user.getMfaEnabled()),
                EmailMaskUtil.mask(user.getEmail()),
                hasEmail));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSettings(@Valid @RequestBody AdminMfaUpdateRequest req) {
        User user = currentAdmin();
        if (Boolean.TRUE.equals(req.getEnabled())) {
            if (req.getEmail() != null && !req.getEmail().isBlank()) {
                user.setEmail(req.getEmail().trim());
            }
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("Khi bật MFA cần nhập email nhận mã OTP.");
            }
            user.setMfaEnabled(true);
        } else {
            user.setMfaEnabled(false);
        }
        userRepository.save(user);
        boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();
        return ResponseEntity.ok(new AdminMfaStatusResponse(
                Boolean.TRUE.equals(user.getMfaEnabled()),
                EmailMaskUtil.mask(user.getEmail()),
                hasEmail));
    }

    private User currentAdmin() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Admin không tồn tại"));
    }
}
