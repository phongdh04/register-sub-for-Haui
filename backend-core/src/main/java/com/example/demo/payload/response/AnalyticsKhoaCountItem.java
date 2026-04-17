package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsKhoaCountItem {
    private String maKhoa;
    private String tenKhoa;
    private long soSinhVien;
}
