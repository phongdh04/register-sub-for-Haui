package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ScheduleChangeSetSubmitRequest {

    @NotNull
    private Map<String, Object> payloadDelta;

    private String requestedBy;

    private String note;
}
