package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PreRegistrationIntentResponse {

    private Long id;
    private Long idSinhVien;
    private Long idHocKy;
    private String tenHocKy;
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private Integer priority;
    private String ghiChu;
    private Instant createdAt;
    private Instant updatedAt;
}
