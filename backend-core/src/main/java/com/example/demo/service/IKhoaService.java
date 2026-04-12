package com.example.demo.service;

import com.example.demo.payload.request.KhoaRequest;
import com.example.demo.payload.response.KhoaResponse;

import java.util.List;

/**
 * ISP: Interface ICourseManagement chỉ dành cho Admin.
 * Tách biệt với ICourseRegistration (dành cho SV).
 * Admin mới được gọi các method thêm/sửa/xóa Khoa.
 */
public interface IKhoaService {
    List<KhoaResponse> getAll();
    KhoaResponse getById(Long id);
    KhoaResponse create(KhoaRequest request);
    KhoaResponse update(Long id, KhoaRequest request);
    void delete(Long id);
}
