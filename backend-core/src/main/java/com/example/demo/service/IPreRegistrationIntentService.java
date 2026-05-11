package com.example.demo.service;

import com.example.demo.payload.request.PreRegistrationIntentSubmitRequest;
import com.example.demo.payload.response.PreRegistrationIntentResponse;

import java.util.List;

/**
 * ISP: API cho sinh viên thao tác trên {@link com.example.demo.domain.entity.PreRegistrationIntent}.
 *
 * <p>Tất cả method nhận {@code username} (từ Authentication) để resolve sang sinh viên,
 * tránh lộ idSinhVien qua URL.
 */
public interface IPreRegistrationIntentService {

    PreRegistrationIntentResponse submit(String username, PreRegistrationIntentSubmitRequest request);

    PreRegistrationIntentResponse update(String username, Long intentId, PreRegistrationIntentSubmitRequest request);

    void delete(String username, Long intentId);

    List<PreRegistrationIntentResponse> listMine(String username, Long idHocKy);
}
