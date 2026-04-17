package com.example.demo.controller;

import com.example.demo.payload.request.CreateRetakeAppealRequest;
import com.example.demo.payload.response.RetakeAppealRowResponse;
import com.example.demo.service.IRetakeAppealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Task 18 – Sinh viên nộp yêu cầu phúc khảo điểm.
 */
@RestController
@RequestMapping("/api/v1/retake-appeals")
@RequiredArgsConstructor
public class RetakeAppealController {

    private final IRetakeAppealService retakeAppealService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RetakeAppealRowResponse> submit(
            Authentication authentication,
            @Valid @RequestBody CreateRetakeAppealRequest body) {
        return ResponseEntity.ok(retakeAppealService.submitAppeal(authentication.getName(), body));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<RetakeAppealRowResponse>> myAppeals(Authentication authentication) {
        return ResponseEntity.ok(retakeAppealService.listMyAppeals(authentication.getName()));
    }
}
