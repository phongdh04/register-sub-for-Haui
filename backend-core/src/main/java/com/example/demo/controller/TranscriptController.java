package com.example.demo.controller;

import com.example.demo.payload.response.TranscriptResponse;
import com.example.demo.service.ITranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 4 – Bảng điểm / Transcript cho sinh viên.
 */
@RestController
@RequestMapping("/api/v1/transcript")
@RequiredArgsConstructor
public class TranscriptController {

    private final ITranscriptService transcriptService;

    /**
     * @param hocKyId tùy chọn: lọc theo một học kỳ; bỏ trống = toàn bộ lịch sử.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TranscriptResponse> getMyTranscript(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(transcriptService.getMyTranscript(authentication.getName(), hocKyId));
    }
}
