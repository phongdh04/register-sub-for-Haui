package com.example.demo.service;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.payload.request.TaiKhoanRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.payload.response.TaiKhoanResponse;
import org.springframework.data.domain.Pageable;

public interface ITaiKhoanService {
    PagedResponse<TaiKhoanResponse> getByRole(Role role, Pageable pageable);
    PagedResponse<TaiKhoanResponse> searchByRole(Role role, String query, Pageable pageable);
    TaiKhoanResponse getById(Long id);
    TaiKhoanResponse create(TaiKhoanRequest request);
    TaiKhoanResponse update(Long id, TaiKhoanRequest request);
    void updateTrangThai(Long id, Status trangThai);
    void delete(Long id);
}
