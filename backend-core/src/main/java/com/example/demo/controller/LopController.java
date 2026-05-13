package com.example.demo.controller;

import com.example.demo.payload.request.LopRequest;
import com.example.demo.payload.response.LopResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.service.ILopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lop")
@RequiredArgsConstructor
public class LopController {

    private final ILopService lopService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LopResponse>> getAll() {
        return ResponseEntity.ok(lopService.getAll());
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<LopResponse>> getAllPaged(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LopResponse> page = lopService.getAllPaged(pageable);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lopService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopResponse> create(@Valid @RequestBody LopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lopService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopResponse> update(@PathVariable Long id, @Valid @RequestBody LopRequest request) {
        return ResponseEntity.ok(lopService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
