package com.example.demo.service;

import com.example.demo.payload.response.ForecastMoLopVersionStatusResponse;
import com.example.demo.payload.response.ForecastShellSpawnResponse;

/** BACK-TKB-016 / 017 — duyệt dự báo & sinh LHP shell. */
public interface IForecastMoLopWorkflowService {

    ForecastMoLopVersionStatusResponse approve(Long hocKyId, Long versionId);

    ForecastMoLopVersionStatusResponse reject(Long hocKyId, Long versionId);

    ForecastShellSpawnResponse spawnShellLhps(Long hocKyId, Long versionId);
}
