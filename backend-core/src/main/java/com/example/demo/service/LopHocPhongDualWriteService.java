package com.example.demo.service;

import com.example.demo.domain.entity.LopHocPhan;

/**
 * BACK-TKB-009 — Đồng bộ {@link com.example.demo.domain.entity.PhongHoc} (FK)
 * & chuỗi {@code phong} trong {@code thoiKhoaBieuJson}.
 */
public interface LopHocPhongDualWriteService {

    /**
     * Khi FK đặt được: ghi canonical {@code maPhong} vào slot chính (index 0) trong JSON nếu có.
     * Khi FK null: cố khớp phòng đầu tiên trong JSON UNIQUE → set FK (optional fill).
     */
    void synchronize(LopHocPhan lhp);
}
