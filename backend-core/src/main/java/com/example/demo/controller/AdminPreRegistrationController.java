package com.example.demo.controller;

import com.example.demo.payload.request.AdminPreRegistrationLinkCreateRequest;
import com.example.demo.payload.response.AdminPreRegistrationLinkCreateResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkItemResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkStatsResponse;
import com.example.demo.security.jwt.UserDetailsImpl;
import com.example.demo.service.IPublicPreRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pre-reg/links")
@RequiredArgsConstructor
public class AdminPreRegistrationController {

    private final IPublicPreRegistrationService preRegistrationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminPreRegistrationLinkCreateResponse> create(@Valid @RequestBody AdminPreRegistrationLinkCreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preRegistrationService.createLink(body, currentUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminPreRegistrationLinkItemResponse>> list() {
        return ResponseEntity.ok(preRegistrationService.listLinks());
    }

    @PutMapping("/{linkId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminPreRegistrationLinkItemResponse> close(@PathVariable Long linkId) {
        return ResponseEntity.ok(preRegistrationService.closeLink(linkId));
    }

    @GetMapping("/{linkId}/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminPreRegistrationLinkStatsResponse> stats(@PathVariable Long linkId) {
        return ResponseEntity.ok(preRegistrationService.getLinkStats(linkId));
    }

    private String currentUsername() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}
