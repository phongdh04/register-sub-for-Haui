package com.example.demo.controller;

import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.payload.request.RegistrationWindowOpenNowRequest;
import com.example.demo.payload.request.RegistrationWindowUpsertRequest;
import com.example.demo.payload.response.RegistrationWindowResponse;
import com.example.demo.security.jwt.UserDetailsImpl;
import com.example.demo.service.IRegistrationWindowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin — quản lý cấu hình {@code registration_window} theo cohort/ngành.
 *
 * <p>Sprint 1 — bổ trợ luồng đăng ký theo cohort. Endpoint cũ
 * {@code PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky} vẫn giữ nguyên làm fallback.
 */
@RestController
@RequestMapping("/api/v1/admin/registration-windows")
@RequiredArgsConstructor
public class AdminRegistrationWindowController {

    private final IRegistrationWindowService registrationWindowService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationWindowResponse>> list(
            @RequestParam Long hocKyId,
            @RequestParam(required = false) RegistrationPhase phase) {
        return ResponseEntity.ok(registrationWindowService.list(hocKyId, phase));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationWindowResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationWindowService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationWindowResponse> create(
            @Valid @RequestBody RegistrationWindowUpsertRequest body,
            Authentication auth) {
        String createdBy = resolveUsername(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationWindowService.create(body, createdBy));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationWindowResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationWindowUpsertRequest body) {
        return ResponseEntity.ok(registrationWindowService.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationWindowService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Quick-action: mở phiên đăng ký ngay lập tức cho một (hocKy, phase, cohort, ngành).
     * Nếu đã có window cùng scope sẽ cập nhật {@code openAt = now}.
     */
    @PostMapping("/open-now")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationWindowResponse> openNow(
            @Valid @RequestBody RegistrationWindowOpenNowRequest body,
            Authentication auth) {
        String createdBy = resolveUsername(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationWindowService.openNow(body, createdBy));
    }

    private static String resolveUsername(Authentication auth) {
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetailsImpl uds) {
            return uds.getUsername();
        }
        return auth.getName();
    }
}
