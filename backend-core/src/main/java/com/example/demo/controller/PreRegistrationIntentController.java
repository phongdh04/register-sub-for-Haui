package com.example.demo.controller;

import com.example.demo.payload.request.PreRegistrationIntentSubmitRequest;
import com.example.demo.payload.response.PreRegistrationIntentResponse;
import com.example.demo.service.IPreRegistrationIntentService;
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
 * Sinh viên — quản lý nguyện vọng đăng ký dự kiến (pha A).
 *
 * <p>Lưu ý: chỉ tạo/sửa/xóa được khi pha PRE đang mở cho khóa/ngành sinh viên.
 */
@RestController
@RequestMapping("/api/v1/pre-registrations/intents")
@RequiredArgsConstructor
public class PreRegistrationIntentController {

    private final IPreRegistrationIntentService preRegistrationIntentService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PreRegistrationIntentResponse>> listMine(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(preRegistrationIntentService.listMine(authentication.getName(), hocKyId));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PreRegistrationIntentResponse> submit(
            Authentication authentication,
            @Valid @RequestBody PreRegistrationIntentSubmitRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preRegistrationIntentService.submit(authentication.getName(), body));
    }

    @PutMapping("/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PreRegistrationIntentResponse> update(
            Authentication authentication,
            @PathVariable Long intentId,
            @Valid @RequestBody PreRegistrationIntentSubmitRequest body) {
        return ResponseEntity.ok(
                preRegistrationIntentService.update(authentication.getName(), intentId, body));
    }

    @DeleteMapping("/{intentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long intentId) {
        preRegistrationIntentService.delete(authentication.getName(), intentId);
        return ResponseEntity.noContent().build();
    }
}
