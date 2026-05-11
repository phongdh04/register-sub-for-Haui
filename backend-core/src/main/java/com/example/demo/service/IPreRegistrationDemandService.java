package com.example.demo.service;

import com.example.demo.payload.response.PreRegistrationDemandResponse;

/**
 * ISP: Tổng hợp demand đăng ký dự kiến cho admin dashboard.
 */
public interface IPreRegistrationDemandService {

    /**
     * Tổng hợp demand cho 1 học kỳ. Có thể lọc theo cohort/ngành (nullable).
     *
     * @param hocKyId         bắt buộc
     * @param namNhapHoc      cohort filter, null = tổng hợp mọi cohort
     * @param idNganh         ngành filter, null = tổng hợp mọi ngành
     * @param targetClassSize dùng để tính số lớp đề xuất (ceil); null/0 = dùng default 40
     */
    PreRegistrationDemandResponse aggregate(Long hocKyId, Integer namNhapHoc, Long idNganh, Integer targetClassSize);
}
