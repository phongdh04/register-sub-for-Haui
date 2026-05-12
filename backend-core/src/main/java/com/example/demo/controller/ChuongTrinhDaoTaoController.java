package com.example.demo.controller;

import com.example.demo.payload.request.ChuongTrinhDaoTaoRequest;
import com.example.demo.payload.request.CtdtHocPhanRequest;
import com.example.demo.payload.response.ChuongTrinhDaoTaoResponse;
import com.example.demo.payload.response.CtdtHocPhanResponse;
import com.example.demo.service.IChuongTrinhDaoTaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chuong-trinh-dao-tao")
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoController {

    private final IChuongTrinhDaoTaoService ctdtService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChuongTrinhDaoTaoResponse>> getAll() {
        return ResponseEntity.ok(ctdtService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChuongTrinhDaoTaoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ctdtService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChuongTrinhDaoTaoResponse> create(@Valid @RequestBody ChuongTrinhDaoTaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ctdtService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChuongTrinhDaoTaoResponse> update(@PathVariable Long id, @Valid @RequestBody ChuongTrinhDaoTaoRequest req) {
        return ResponseEntity.ok(ctdtService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ctdtService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* --- Mapping HP vào CTĐT --- */

    @GetMapping("/{id}/hoc-phan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CtdtHocPhanResponse>> getHocPhan(@PathVariable Long id) {
        return ResponseEntity.ok(ctdtService.getHocPhanByCtdt(id));
    }

    @PostMapping("/hoc-phan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CtdtHocPhanResponse> addHocPhan(@Valid @RequestBody CtdtHocPhanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ctdtService.addHocPhan(req));
    }

    @DeleteMapping("/hoc-phan/{idCtdtHp}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeHocPhan(@PathVariable Long idCtdtHp) {
        ctdtService.removeHocPhan(idCtdtHp);
        return ResponseEntity.noContent().build();
    }
}
