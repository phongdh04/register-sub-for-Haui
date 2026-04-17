package com.example.demo.service;

import com.example.demo.payload.request.LecturerUpdateGradeRequest;
import com.example.demo.payload.response.GradebookRowResponse;
import com.example.demo.payload.response.LecturerGradebookResponse;

public interface IGradingService {

    LecturerGradebookResponse getGradebook(String username, Long idLopHp);

    GradebookRowResponse saveDraftGrade(String username, Long idDangKy, LecturerUpdateGradeRequest request);

    GradebookRowResponse publishGrade(String username, Long idDangKy);
}
