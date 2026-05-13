package com.example.demo.service;

import com.example.demo.payload.request.RegistrationCampaignRequest;
import com.example.demo.payload.response.RegistrationCampaignResponse;

import java.util.List;

/**
 * Service quản lý chiến dịch đăng ký theo khóa.
 * Khi tạo campaign, hệ thống tự động tạo {@code RegistrationWindow}
 * cho từng {@code HocKy} phù hợp với khóa đó.
 */
public interface IRegistrationCampaignService {

    /**
     * Tao chiến dịch đăng ký + auto-generate RegistrationWindow.
     * Neu da co campaign cho (namNhapHoc, phase) -> throw 409.
     */
    RegistrationCampaignResponse create(RegistrationCampaignRequest request, String createdBy);

    /**
     * Cap nhat chiến dịch + dong bo tat ca windows thuoc campaign.
     */
    RegistrationCampaignResponse update(Long id, RegistrationCampaignRequest request);

    /**
     * Xoa chiến dịch + tat ca RegistrationWindow thuoc no.
     */
    void delete(Long id);

    /**
     * Chi tiet chiến dịch.
     */
    RegistrationCampaignResponse getById(Long id);

    /**
     * Danh sach tat ca chiến dịch.
     */
    List<RegistrationCampaignResponse> listAll();

    /**
     * Danh sach tat ca chiến dịch đang mở.
     */
    List<RegistrationCampaignResponse> listActive();
}
