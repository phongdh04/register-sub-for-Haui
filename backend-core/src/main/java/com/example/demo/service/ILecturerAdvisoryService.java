package com.example.demo.service;

import com.example.demo.payload.response.AdvisoryAtRiskListResponse;

public interface ILecturerAdvisoryService {

    /**
     * Danh sách SV cùng khoa với giảng viên đăng nhập, có tổng tín chỉ rớt (điểm công bố) ≥ ngưỡng.
     */
    AdvisoryAtRiskListResponse listAtRiskStudents(String lecturerUsername, int minFailedCredits);
}
