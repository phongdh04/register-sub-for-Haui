package com.example.demo.service;

import com.example.demo.payload.request.CreateRetakeAppealRequest;
import com.example.demo.payload.request.LecturerRetakeAppealDecisionRequest;
import com.example.demo.payload.response.RetakeAppealRowResponse;

import java.util.List;

public interface IRetakeAppealService {

    RetakeAppealRowResponse submitAppeal(String studentUsername, CreateRetakeAppealRequest request);

    List<RetakeAppealRowResponse> listMyAppeals(String studentUsername);

    List<RetakeAppealRowResponse> listForLecturer(String lecturerUsername, String trangThai);

    RetakeAppealRowResponse processAppeal(String lecturerUsername, Long idYeuCau, LecturerRetakeAppealDecisionRequest request);
}
