package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TimetableCourseResponse {
    private Long idDangKy;
    private Long idLopHp;
    private String maLopHp;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private String tenGiangVien;
    private List<TimetableSessionResponse> sessions;
}
