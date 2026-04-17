package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TranscriptResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTenSinhVien;

    private BigDecimal gpaTichLuy;
    private int tongTinChiDangKy;
    private int tongTinChiCoDiem;

    private List<TranscriptSemesterResponse> hocKys;
}
