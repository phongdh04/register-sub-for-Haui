package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongJsonAuditSummary {

    @Builder.Default
    private long totalLhpInHocKy = 0;

    @Builder.Default
    private long lhpWithNonNullJson = 0;

    @Builder.Default
    private long lhpWithAnyPhongKey = 0;

    @Builder.Default
    private long categoryNoJson = 0;

    @Builder.Default
    private long categoryEmptyJson = 0;

    @Builder.Default
    private long categoryNoPhongKeys = 0;

    @Builder.Default
    private long categoryAllSlotsUnique = 0;

    @Builder.Default
    private long categoryHasAmbiguous = 0;

    @Builder.Default
    private long categoryHasNoMatch = 0;

    @Builder.Default
    private long categoryMixed = 0;
}
