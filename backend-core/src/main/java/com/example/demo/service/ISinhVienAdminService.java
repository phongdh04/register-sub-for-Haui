package com.example.demo.service;

import com.example.demo.payload.request.SinhVienRequest;
import com.example.demo.payload.response.SinhVienResponse;
import java.util.List;

public interface ISinhVienAdminService {
    List<SinhVienResponse> getAll();
    SinhVienResponse getById(Long id);
    SinhVienResponse create(SinhVienRequest request);
    SinhVienResponse update(Long id, SinhVienRequest request);
    void delete(Long id);
}
