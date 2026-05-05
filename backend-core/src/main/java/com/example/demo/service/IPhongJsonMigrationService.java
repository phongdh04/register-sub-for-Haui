package com.example.demo.service;

import com.example.demo.payload.response.PhongJsonApplyResponse;
import com.example.demo.payload.response.PhongJsonAuditResponse;

public interface IPhongJsonMigrationService {

    PhongJsonAuditResponse audit(Long hocKyId, boolean includeDetails);

    PhongJsonApplyResponse apply(Long hocKyId, boolean dryRun);
}
