package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongJsonApplyResponse {

    private Long hocKyId;
    private boolean dryRun;
    private int examinedLhp;
    private int updatedFkCount;
    private int skippedAlreadyHadFk;
    private int skippedNoJson;
    private int skippedNoResolvablePhong;
    private List<Long> unresolvedLopHpIds;
    private List<Long> ambiguousLopHpIds;
}
