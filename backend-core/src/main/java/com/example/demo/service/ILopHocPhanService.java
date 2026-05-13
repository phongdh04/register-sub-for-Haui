package com.example.demo.service;

import com.example.demo.payload.request.LopHocPhanRequest;
import com.example.demo.payload.response.LopHocPhanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ISP: Interface quản lý Lớp Học Phần.
 * Admin: CRUD lớp, phát hành lớp (mở đăng ký).
 */
public interface ILopHocPhanService {
    List<LopHocPhanResponse> getAllByHocKy(Long idHocKy);
    Page<LopHocPhanResponse> getAllByHocKyPaged(Long idHocKy, Pageable pageable);
    LopHocPhanResponse getById(Long id);
    LopHocPhanResponse create(LopHocPhanRequest request);
    LopHocPhanResponse update(Long id, LopHocPhanRequest request);
    void delete(Long id);

    /**
     * Phát hành lớp - chuyển trạng thái sang DANG_MO.
     * Warm-up Redis slot khi gọi API này.
     */
    LopHocPhanResponse publishLop(Long id);
    LopHocPhanResponse closeLop(Long id);
}
