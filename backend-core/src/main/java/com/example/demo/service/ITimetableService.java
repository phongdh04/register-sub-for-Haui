package com.example.demo.service;

import com.example.demo.payload.response.TimetableResponse;

public interface ITimetableService {
    TimetableResponse getMyTimetable(String username, Long hocKyId);
}
