package com.example.demo.service;

import com.example.demo.payload.request.GiangVienRequest;
import com.example.demo.payload.response.GiangVienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ISP: Interface riêng cho quản lý Giảng Viên.
 */
public interface IGiangVienService {
    List<GiangVienResponse> getAll();
    Page<GiangVienResponse> getAllPaged(Pageable pageable);
    GiangVienResponse getById(Long id);
    List<GiangVienResponse> getByKhoa(Long idKhoa);
    Page<GiangVienResponse> getByKhoaPaged(Long idKhoa, Pageable pageable);
    GiangVienResponse create(GiangVienRequest request);
    GiangVienResponse update(Long id, GiangVienRequest request);
    void delete(Long id);
}
