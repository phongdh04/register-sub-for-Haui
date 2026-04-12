package com.example.demo.service;

import com.example.demo.payload.request.HocKyRequest;
import com.example.demo.payload.response.HocKyResponse;

import java.util.List;

/**
 * ISP: Interface riêng cho quản lý Học Kỳ.
 */
public interface IHocKyService {
    List<HocKyResponse> getAll();
    HocKyResponse getById(Long id);
    HocKyResponse getCurrent();
    HocKyResponse create(HocKyRequest request);
    HocKyResponse setActive(Long id);
    void delete(Long id);
}
