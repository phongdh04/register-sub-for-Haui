package com.example.demo.controller;

import com.example.demo.payload.response.DegreeAuditResponse;
import com.example.demo.service.IDegreeAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 3 - Degree Audit (Cây khung chương trình + tiến độ).
 */
@RestController
@RequestMapping("/api/v1/degree-audit")
@RequiredArgsConstructor
public class DegreeAuditController {

    private final IDegreeAuditService degreeAuditService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<DegreeAuditResponse> getMyDegreeAudit(Authentication authentication) {
        return ResponseEntity.ok(degreeAuditService.getMyDegreeAudit(authentication.getName()));
    }
}

