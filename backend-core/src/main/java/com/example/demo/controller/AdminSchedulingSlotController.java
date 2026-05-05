package com.example.demo.controller;

import com.example.demo.payload.request.SchedulingSlotPatchRequest;
import com.example.demo.payload.response.SchedulingSlotPatchResponse;
import com.example.demo.service.ISchedulingGridAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BACK-TKB-034 — patch slot theo idLopHp. */
@RestController
@RequestMapping("/api/v1/admin/scheduling/lop-hoc-phan")
@RequiredArgsConstructor
public class AdminSchedulingSlotController {

    private final ISchedulingGridAdminService schedulingGridAdminService;

    @PatchMapping("/{idLopHp}/slot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchedulingSlotPatchResponse> patchSlot(
            @PathVariable Long idLopHp,
            @Valid @RequestBody SchedulingSlotPatchRequest request) {
        return ResponseEntity.ok(schedulingGridAdminService.patchSlot(idLopHp, request));
    }
}
