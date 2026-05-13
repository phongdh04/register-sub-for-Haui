package com.example.demo.service;

import com.example.demo.payload.request.HocPhanRequest;
import com.example.demo.payload.response.HocPhanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ISP: Interface quản lý Học Phần (Master Data).
 * Admin dùng để CRUD môn học.
 */
public interface IHocPhanService {
    List<HocPhanResponse> getAll();
    Page<HocPhanResponse> getAllPaged(Pageable pageable);
    HocPhanResponse getById(Long id);
    HocPhanResponse getByMa(String maHocPhan);
    HocPhanResponse create(HocPhanRequest request);
    HocPhanResponse update(Long id, HocPhanRequest request);
    void delete(Long id);
}
