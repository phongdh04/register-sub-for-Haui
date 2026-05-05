package com.example.demo.service;

import com.example.demo.payload.request.AdminPreRegistrationLinkCreateRequest;
import com.example.demo.payload.request.PublicPreRegistrationSubmitRequest;
import com.example.demo.payload.response.AdminPreRegistrationLinkCreateResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkItemResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkStatsResponse;
import com.example.demo.payload.response.PublicPreRegistrationLinkResponse;
import com.example.demo.payload.response.PublicPreRegistrationRequestStatusResponse;
import com.example.demo.payload.response.PublicPreRegistrationSubmitResponse;

import java.util.UUID;
import java.util.List;

public interface IPublicPreRegistrationService {
    PublicPreRegistrationLinkResponse getLinkInfo(String token);

    PublicPreRegistrationSubmitResponse submit(String token, PublicPreRegistrationSubmitRequest request, String sourceIp, String userAgent);

    PublicPreRegistrationRequestStatusResponse getRequestStatus(UUID requestId);

    AdminPreRegistrationLinkCreateResponse createLink(AdminPreRegistrationLinkCreateRequest request, String createdBy);

    List<AdminPreRegistrationLinkItemResponse> listLinks();

    AdminPreRegistrationLinkItemResponse closeLink(Long linkId);

    AdminPreRegistrationLinkStatsResponse getLinkStats(Long linkId);
}
