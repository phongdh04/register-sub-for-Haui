package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AtRiskStudentListResponse {
    private Long idKhoa;
    private String tenKhoa;
    private int nguongTinChiRot;
    private int tongSoBanGhi;
    private List<AtRiskStudentRowResponse> rows;
}
