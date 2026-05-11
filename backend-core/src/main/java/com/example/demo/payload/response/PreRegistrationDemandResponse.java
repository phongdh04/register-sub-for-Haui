package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PreRegistrationDemandResponse {

    private Long idHocKy;
    private String tenHocKy;
    private Integer namNhapHoc;
    private Long idNganh;
    private String tenNganh;

    private int targetClassSize;

    private long totalIntents;
    private int totalRecommendedClasses;

    private List<PreRegistrationDemandItemResponse> items;
}
