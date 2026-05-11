package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

/**
 * 1 dòng demand tổng hợp cho admin: nhu cầu đăng ký dự kiến của 1 học phần
 * trong phạm vi (cohort × ngành) đã chọn.
 */
@Data
@Builder
public class PreRegistrationDemandItemResponse {

    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;

    private Integer namNhapHoc;
    private Long idNganh;
    private String tenNganh;

    /** Tổng số sinh viên đăng ký dự kiến cho học phần này trong phạm vi đã chọn. */
    private long totalIntent;

    /** Số lớp đề xuất mở (ceil(totalIntent / targetClassSize)). */
    private int recommendedClasses;
}
