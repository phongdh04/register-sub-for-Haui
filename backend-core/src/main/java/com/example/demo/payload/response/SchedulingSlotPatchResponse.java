package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulingSlotPatchResponse {
    private Long hocKyId;
    private Long idLopHp;
    private boolean persisted;
    private long revisionVersion;
    private String message;
}
