package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamScheduleResponse {
    private Long idHocKy;
    private String hocKyLabel;
    private int tongMonCoDangKy;
    private int tongMonCoLichThi;
    private List<ExamScheduleRowResponse> rows;
}
