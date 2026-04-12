package com.example.demo.service;

import com.example.demo.payload.request.HocPhanRequest;
import com.example.demo.payload.response.HocPhanResponse;

import java.util.List;

/**
 * ISP: Interface quản lý Học Phần (Master Data).
 * Admin dùng để CRUD môn học.
 */
public interface IHocPhanService {
    List<HocPhanResponse> getAll();
    HocPhanResponse getById(Long id);
    HocPhanResponse getByMa(String maHocPhan);
    HocPhanResponse create(HocPhanRequest request);
    HocPhanResponse update(Long id, HocPhanRequest request);
    void delete(Long id);
}
