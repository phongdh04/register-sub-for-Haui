package com.example.demo.controller;

import com.example.demo.payload.request.PreRegistrationPlanSectionsRequest;
import com.example.demo.payload.response.PreRegistrationDemandResponse;
import com.example.demo.payload.response.PreRegistrationPlanSectionsResponse;
import com.example.demo.service.IPreRegistrationDemandService;
import com.example.demo.service.IPreRegistrationPlanSectionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin — dashboard demand đăng ký dự kiến (pha A).
 *
 * <p>Namespace tách biệt với {@code /api/v1/admin/pre-reg/links}
 * (đó là pre-reg tuyển sinh, không phải intent đăng ký môn).
 *
 * <p>{@code POST /plan-sections} (F17): sinh shell {@code lop_hoc_phan} từ demand PRE hoặc {@code sectionCount}.
 */
@RestController
@RequestMapping("/api/v1/admin/pre-registrations")
@RequiredArgsConstructor
public class AdminPreRegistrationDemandController {

    private final IPreRegistrationDemandService preRegistrationDemandService;
    private final IPreRegistrationPlanSectionsService preRegistrationPlanSectionsService;

    @GetMapping("/demand")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PreRegistrationDemandResponse> demand(
            @RequestParam Long hocKyId,
            @RequestParam(required = false) Integer namNhapHoc,
            @RequestParam(required = false) Long idNganh,
            @RequestParam(required = false) Integer targetClassSize) {
        return ResponseEntity.ok(
                preRegistrationDemandService.aggregate(hocKyId, namNhapHoc, idNganh, targetClassSize));
    }

    @PostMapping("/plan-sections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PreRegistrationPlanSectionsResponse> planSections(
            @Valid @RequestBody PreRegistrationPlanSectionsRequest body,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        PreRegistrationPlanSectionsResponse res = preRegistrationPlanSectionsService.planSections(body, idempotencyKey);
        HttpStatus status = res.isIdempotentReplay() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(res);
    }
}
