package com.example.demo.service;

import com.example.demo.payload.request.GiangVienRequest;
import com.example.demo.payload.response.GiangVienResponse;

import java.util.List;

/**
 * ISP: Interface riêng cho quản lý Giảng Viên.
 */
public interface IGiangVienService {
    List<GiangVienResponse> getAll();
    GiangVienResponse getById(Long id);
    List<GiangVienResponse> getByKhoa(Long idKhoa);
    GiangVienResponse create(GiangVienRequest request);
    GiangVienResponse update(Long id, GiangVienRequest request);
    void delete(Long id);
}
