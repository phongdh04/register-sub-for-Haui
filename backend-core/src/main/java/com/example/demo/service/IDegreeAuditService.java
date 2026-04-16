package com.example.demo.service;

import com.example.demo.payload.response.DegreeAuditResponse;

public interface IDegreeAuditService {
    DegreeAuditResponse getMyDegreeAudit(String username);
}

