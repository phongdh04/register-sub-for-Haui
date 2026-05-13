package com.example.demo.service;

import com.example.demo.payload.request.HocKyLichDangKyRequest;
import com.example.demo.payload.request.HocKyRequest;
import com.example.demo.payload.response.HocKyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ISP: Interface riêng cho quản lý Học Kỳ.
 */
public interface IHocKyService {
    List<HocKyResponse> getAll();
    Page<HocKyResponse> getAllPaged(Pageable pageable);
    HocKyResponse getById(Long id);
    HocKyResponse getCurrent();
    HocKyResponse create(HocKyRequest request);
    HocKyResponse setActive(Long id);
    void delete(Long id);

    /** Admin: cập nhật lịch đăng ký trước / đăng ký chính thức theo học kỳ. */
    HocKyResponse updateLichDangKy(Long id, HocKyLichDangKyRequest request);
}
