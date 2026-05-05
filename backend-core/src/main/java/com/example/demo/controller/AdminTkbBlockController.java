package com.example.demo.controller;

import com.example.demo.payload.request.TkbBlockUpsertRequest;
import com.example.demo.payload.response.TkbBlockResponse;
import com.example.demo.service.ITkbBlockAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** BACK-TKB-037 — lifecycle API TkbBlock theo học kỳ. */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}/tkb-blocks")
@RequiredArgsConstructor
public class AdminTkbBlockController {

    private final ITkbBlockAdminService tkbBlockAdminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TkbBlockResponse>> list(@PathVariable Long hocKyId) {
        return ResponseEntity.ok(tkbBlockAdminService.listByHocKy(hocKyId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TkbBlockResponse> create(
            @PathVariable Long hocKyId,
            @Valid @RequestBody TkbBlockUpsertRequest body) {
        return ResponseEntity.ok(tkbBlockAdminService.create(hocKyId, body));
    }

    @PutMapping("/{idTkbBlock}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TkbBlockResponse> update(
            @PathVariable Long hocKyId,
            @PathVariable Long idTkbBlock,
            @Valid @RequestBody TkbBlockUpsertRequest body) {
        return ResponseEntity.ok(tkbBlockAdminService.update(hocKyId, idTkbBlock, body));
    }

    @DeleteMapping("/{idTkbBlock}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long hocKyId, @PathVariable Long idTkbBlock) {
        tkbBlockAdminService.delete(hocKyId, idTkbBlock);
        return ResponseEntity.noContent().build();
    }
}
