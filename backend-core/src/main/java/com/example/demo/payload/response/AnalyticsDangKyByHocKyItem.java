package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsDangKyByHocKyItem {
    private Long idHocKy;
    private String namHoc;
    private Integer kyThu;
    private String hocKyLabel;
    private long soDangKyHieuLuc;
}
