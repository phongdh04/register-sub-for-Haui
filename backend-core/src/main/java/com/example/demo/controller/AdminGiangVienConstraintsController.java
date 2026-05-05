package com.example.demo.controller;

import com.example.demo.payload.request.GvBusySlotUpsertRequest;
import com.example.demo.payload.response.GiangVienConstraintsResponse;
import com.example.demo.payload.response.GvBusySlotResponse;
import com.example.demo.service.IGiangVienConstraintsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin — ràng buộc GV: busy slot (BACK-TKB-006).
 */
@RestController
@RequestMapping("/api/v1/admin/giang-vien/{giangVienId}/constraints")
@RequiredArgsConstructor
public class AdminGiangVienConstraintsController {

    private final IGiangVienConstraintsService giangVienConstraintsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiangVienConstraintsResponse> getConstraints(
            @PathVariable Long giangVienId,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(giangVienConstraintsService.getConstraints(giangVienId, hocKyId));
    }

    @PostMapping("/busy-slots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GvBusySlotResponse> createBusySlot(
            @PathVariable Long giangVienId,
            @Valid @RequestBody GvBusySlotUpsertRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(giangVienConstraintsService.createBusySlot(giangVienId, body));
    }

    @PutMapping("/busy-slots/{slotId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GvBusySlotResponse> updateBusySlot(
            @PathVariable Long giangVienId,
            @PathVariable Long slotId,
            @Valid @RequestBody GvBusySlotUpsertRequest body) {
        return ResponseEntity.ok(giangVienConstraintsService.updateBusySlot(giangVienId, slotId, body));
    }

    @DeleteMapping("/busy-slots/{slotId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBusySlot(
            @PathVariable Long giangVienId, @PathVariable Long slotId) {
        giangVienConstraintsService.deleteBusySlot(giangVienId, slotId);
        return ResponseEntity.noContent().build();
    }
}
