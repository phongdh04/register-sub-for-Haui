package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TranscriptSemesterResponse {
    private Long idHocKy;
    private String tenHocKy;
    private int tongTinChiDangKy;
    private int tongTinChiCoDiem;
    private BigDecimal gpaHocKy;
    private List<TranscriptLineResponse> monHoc;
}
