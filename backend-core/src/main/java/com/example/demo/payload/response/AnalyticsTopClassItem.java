package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsTopClassItem {
    private String maLopHp;
    private String tenHocPhan;
    private int siSoToiDa;
    private int siSoThucTe;
    private int tyLePhanTram;
}
