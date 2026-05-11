package com.example.demo.controller;

import com.example.demo.payload.response.RegistrationStudentResponse;
import com.example.demo.payload.response.RegistrationWindowStatusResponse;
import com.example.demo.service.IRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Sinh vien dang ky chinh thuc hoc phan qua REST (sync, khong qua Kafka).
 *
 * <p>Endpoint:
 * <ul>
 *   <li>{@code GET  /api/v1/registrations/me?hocKyId=...} — danh sach lop dang dang ky.</li>
 *   <li>{@code GET  /api/v1/registrations/me/window-status?hocKyId=...} — trang thai phien.</li>
 *   <li>{@code POST /api/v1/registrations?idLopHp=...&hocKyId=...} — dang ky 1 lop.</li>
 *   <li>{@code DELETE /api/v1/registrations/{idDangKy}} — huy 1 dang ky.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<RegistrationStudentResponse.RegisteredItem>> listMine(
            Authentication auth,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(registrationService.listMine(auth.getName(), hocKyId));
    }

    @GetMapping("/me/window-status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RegistrationWindowStatusResponse> getWindowStatus(
            Authentication auth,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(registrationService.getMyWindowStatus(auth.getName(), hocKyId));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RegistrationStudentResponse> register(
            Authentication auth,
            @RequestParam Long idLopHp,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.register(auth.getName(), idLopHp, hocKyId));
    }

    @DeleteMapping("/{idDangKy}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancel(
            Authentication auth,
            @PathVariable Long idDangKy) {
        registrationService.cancel(auth.getName(), idDangKy);
        return ResponseEntity.noContent().build();
    }
}
