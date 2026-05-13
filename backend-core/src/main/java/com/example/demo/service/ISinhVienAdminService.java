package com.example.demo.service;

import com.example.demo.payload.request.SinhVienRequest;
import com.example.demo.payload.response.SinhVienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISinhVienAdminService {
    List<SinhVienResponse> getAll();
    Page<SinhVienResponse> getAllPaged(Pageable pageable);
    SinhVienResponse getById(Long id);
    SinhVienResponse create(SinhVienRequest request);
    SinhVienResponse update(Long id, SinhVienRequest request);
    void delete(Long id);
}
