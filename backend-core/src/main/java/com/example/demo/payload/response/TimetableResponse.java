package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TimetableResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTenSinhVien;
    private Long idHocKy;
    private String tenHocKy;
    private int tongMonDangKy;
    private int tongTinChi;
    private List<TimetableCourseResponse> courses;
}
