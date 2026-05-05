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
public class SchedulingSnapshotResponse {

    /** Học kỳ của snapshot */
    private Long hocKyId;
    /** Theo cột DB {@code tkb_revision}; client gửi lại trong conflict-check nếu cần stale guard */
    private long revisionVersion;
    /** HOT cache hit → WARM; miss/rebuild → COLD */
    private String cachePhase;
    private List<SchedulingSnapshotRowResponse> rows;
}
