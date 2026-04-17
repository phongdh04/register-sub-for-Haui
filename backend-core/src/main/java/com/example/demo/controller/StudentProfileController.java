package com.example.demo.controller;

import com.example.demo.payload.request.StudentContactPatchRequest;
import com.example.demo.payload.response.StudentProfileResponse;
import com.example.demo.service.IStudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 2 – Tra cứu / cập nhật hồ sơ cá nhân sinh viên.
 */
@RestController
@RequestMapping("/api/v1/student-profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final IStudentProfileService studentProfileService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(studentProfileService.getMyProfile(authentication.getName()));
    }

    @PatchMapping("/me/contact")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> patchMyContact(
            Authentication authentication,
            @Valid @RequestBody StudentContactPatchRequest body) {
        return ResponseEntity.ok(studentProfileService.patchMyContact(authentication.getName(), body));
    }
}
