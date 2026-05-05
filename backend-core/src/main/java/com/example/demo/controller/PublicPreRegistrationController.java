package com.example.demo.controller;

import com.example.demo.payload.request.PublicPreRegistrationSubmitRequest;
import com.example.demo.payload.response.PublicPreRegistrationLinkResponse;
import com.example.demo.payload.response.PublicPreRegistrationRequestStatusResponse;
import com.example.demo.payload.response.PublicPreRegistrationSubmitResponse;
import com.example.demo.service.IPublicPreRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/v1/pre-reg")
@RequiredArgsConstructor
public class PublicPreRegistrationController {

    private final IPublicPreRegistrationService publicPreRegistrationService;

    @GetMapping("/links/{token}")
    public ResponseEntity<PublicPreRegistrationLinkResponse> getLinkInfo(@PathVariable String token) {
        return ResponseEntity.ok(publicPreRegistrationService.getLinkInfo(token));
    }

    @PostMapping("/links/{token}/submit")
    public ResponseEntity<PublicPreRegistrationSubmitResponse> submit(
            @PathVariable String token,
            @Valid @RequestBody PublicPreRegistrationSubmitRequest body,
            HttpServletRequest request) {
        String sourceIp = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(publicPreRegistrationService.submit(token, body, sourceIp, userAgent));
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<PublicPreRegistrationRequestStatusResponse> getRequestStatus(@PathVariable UUID requestId) {
        return ResponseEntity.ok(publicPreRegistrationService.getRequestStatus(requestId));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
