package com.example.demo.service;

import com.example.demo.payload.request.ForecastMoLopRunRequest;
import com.example.demo.payload.response.ForecastMoLopRunResponse;

/** BACK-TKB-014 / 015 — dự báo số lớp theo học phần theo học kỳ + CTĐT. */
public interface IForecastMoLopService {

    ForecastMoLopRunResponse runForecast(Long hocKyId, ForecastMoLopRunRequest request);
}
