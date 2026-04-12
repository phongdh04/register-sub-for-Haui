package com.example.demo.service;

import com.example.demo.payload.request.NganhDaoTaoRequest;
import com.example.demo.payload.response.NganhDaoTaoResponse;

import java.util.List;

/**
 * ISP: Interface riêng cho quản lý Ngành Đào Tạo.
 * Tách biệt với IKhoaService, không nhồi nhét chung.
 */
public interface INganhDaoTaoService {
    List<NganhDaoTaoResponse> getAll();
    NganhDaoTaoResponse getById(Long id);
    List<NganhDaoTaoResponse> getByKhoa(Long idKhoa);
    NganhDaoTaoResponse create(NganhDaoTaoRequest request);
    NganhDaoTaoResponse update(Long id, NganhDaoTaoRequest request);
    void delete(Long id);
}
