package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DegreeAuditBlockResponse {
    private String khoiKienThuc;
    private int tongTinChi;
    private int tinChiDaHoanThanh;
    private List<DegreeAuditCourseItemResponse> hocPhans;
}

