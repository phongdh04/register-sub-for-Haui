package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingConflictCheckResponse {
    private Long hocKyId;
    private Long idLopHpDraft;
    private long revisionVersion;
    private boolean hasConflict;
    private List<SchedulingConflictItemResponse> conflicts;
}
