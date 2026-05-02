package com.example.demo.controller;

import com.example.demo.payload.response.RegistrationSuggestionResponse;
import com.example.demo.service.IRegistrationSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gợi ý lớp đăng ký theo khóa (năm nhập học) + học kỳ đăng ký và CTĐT — phục vụ giờ G / tham khảo trước khi chọn lớp.
 */
@RestController
@RequestMapping("/api/v1/registration/suggestions")
@RequiredArgsConstructor
public class RegistrationSuggestionController {

    private final IRegistrationSuggestionService registrationSuggestionService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RegistrationSuggestionResponse> suggestForMe(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(
                registrationSuggestionService.suggestForCurrentStudent(authentication.getName(), hocKyId));
    }
}
