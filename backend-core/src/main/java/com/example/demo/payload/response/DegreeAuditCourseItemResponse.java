package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DegreeAuditCourseItemResponse {
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private String loaiMon;
    private boolean batBuoc;
    private Integer hocKyGoiY;
    private boolean daHoanThanh;
}

