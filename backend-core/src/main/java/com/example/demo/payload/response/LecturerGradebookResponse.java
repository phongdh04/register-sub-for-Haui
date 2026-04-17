package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LecturerGradebookResponse {
    private Long idLopHp;
    private String maLopHp;
    private String tenHocPhan;
    private Long idHocKy;
    private String hocKyLabel;
    private int siSo;
    private List<GradebookRowResponse> rows;
}
