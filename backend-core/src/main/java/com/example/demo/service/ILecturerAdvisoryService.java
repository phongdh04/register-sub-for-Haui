package com.example.demo.service;

import com.example.demo.payload.response.AtRiskStudentListResponse;

public interface ILecturerAdvisoryService {

    AtRiskStudentListResponse listAtRiskStudents(String username, Integer minFailTc);
}
