package com.example.demo.payload.response;

import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LopHocPhanPublishResponse {

    private Long idLopHp;
    private String maLopHp;
    private Long idHocKy;
    private String maHocPhan;
    private String tenHocPhan;
    private Long idGiangVien;
    private String tenGiangVien;
    private boolean hasSchedule;
    private LopHocPhanPublishStatus statusPublish;
    private Long version;
    private String message;
}
