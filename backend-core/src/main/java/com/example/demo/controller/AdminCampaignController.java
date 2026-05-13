package com.example.demo.controller;

import com.example.demo.payload.request.RegistrationCampaignRequest;
import com.example.demo.payload.response.RegistrationCampaignResponse;
import com.example.demo.security.jwt.UserDetailsImpl;
import com.example.demo.service.IRegistrationCampaignService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin — quan ly chiến dịch đăng ký theo khóa (cohort).
 *
 * <p>Chiến dịch tự động tạo RegistrationWindow cho từng HocKy
 * phù hợp với khóa sinh viên. API cũ
 * {@code PUT /api/v1/admin/registration-windows} van hoat dong binh thuong.
 */
@RestController
@RequestMapping("/api/v1/admin/registration-campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCampaignController {

    private final IRegistrationCampaignService campaignService;

    /**
     * Danh sach tat ca chiến dịch.
     */
    @GetMapping
    public ResponseEntity<List<RegistrationCampaignResponse>> listAll() {
        return ResponseEntity.ok(campaignService.listAll());
    }

    /**
     * Danh sach chiến dịch đang mở.
     */
    @GetMapping("/active")
    public ResponseEntity<List<RegistrationCampaignResponse>> listActive() {
        return ResponseEntity.ok(campaignService.listActive());
    }

    /**
     * Chi tiet chiến dịch.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationCampaignResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getById(id));
    }

    /**
     * Tao chiến dịch moi + tu dong tao RegistrationWindow.
     */
    @PostMapping
    public ResponseEntity<RegistrationCampaignResponse> create(
            @Valid @RequestBody RegistrationCampaignRequest body,
            Authentication auth) {
        String createdBy = resolveUsername(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.create(body, createdBy));
    }

    /**
     * Cap nhat chiến dịch + dong bo thoi gian tat ca windows thuoc no.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationCampaignResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationCampaignRequest body) {
        return ResponseEntity.ok(campaignService.update(id, body));
    }

    /**
     * Xoa chiến dịch + tat ca RegistrationWindow thuoc no.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
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
