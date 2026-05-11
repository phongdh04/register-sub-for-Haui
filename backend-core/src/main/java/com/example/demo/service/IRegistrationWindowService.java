package com.example.demo.service;

import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.payload.request.RegistrationWindowOpenNowRequest;
import com.example.demo.payload.request.RegistrationWindowUpsertRequest;
import com.example.demo.payload.response.RegistrationWindowResponse;

import java.util.List;

/**
 * ISP: Service quản lý {@code registration_window} (cấu hình thời gian đăng ký theo cohort/ngành).
 */
public interface IRegistrationWindowService {

    RegistrationWindowResponse create(RegistrationWindowUpsertRequest request, String createdBy);

    RegistrationWindowResponse update(Long id, RegistrationWindowUpsertRequest request);

    void delete(Long id);

    RegistrationWindowResponse getById(Long id);

    List<RegistrationWindowResponse> list(Long hocKyId, RegistrationPhase phase);

    /**
     * Mo phien dang ky ngay lap tuc cho 1 pha (PRE/OFFICIAL) + scope (cohort, nganh).
     * Tao window neu chua co; cap nhat openAt = now neu da co window cung scope.
     */
    RegistrationWindowResponse openNow(RegistrationWindowOpenNowRequest request, String createdBy);
}
