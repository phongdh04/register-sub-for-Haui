package com.example.demo.service;

import com.example.demo.payload.response.ExamScheduleResponse;

public interface IExamScheduleService {

    ExamScheduleResponse getMyExamSchedule(String username, Long hocKyId);
}
