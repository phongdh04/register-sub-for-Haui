package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LecturerTeachingClassResponse {
    private Long idLopHp;
    private String maLopHp;
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Long idHocKy;
    private String hocKyLabel;
}
