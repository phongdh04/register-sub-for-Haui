package com.example.demo.controller;

import com.example.demo.payload.request.PreRegCartAddItemRequest;
import com.example.demo.payload.response.PreRegCartItemResponse;
import com.example.demo.payload.response.PreRegCartResponse;
import com.example.demo.service.IPreRegistrationCartService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 5 – Giỏ đăng ký trước giờ G (bản nháp theo học kỳ).
 */
@RestController
@RequestMapping("/api/v1/pre-reg/cart")
@RequiredArgsConstructor
public class PreRegistrationCartController {

    private final IPreRegistrationCartService preRegistrationCartService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PreRegCartResponse> getMyCart(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(preRegistrationCartService.getMyCart(authentication.getName(), hocKyId));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PreRegCartItemResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody PreRegCartAddItemRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preRegistrationCartService.addItem(authentication.getName(), body));
    }

    @DeleteMapping("/items/{idGioHang}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable Long idGioHang) {
        preRegistrationCartService.removeItem(authentication.getName(), idGioHang);
        return ResponseEntity.noContent().build();
    }
}
