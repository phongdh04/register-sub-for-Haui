package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExamScheduleRowResponse {
    private Long idDangKy;
    private Long idLopHp;
    private String maLopHp;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private boolean coLichThi;
    private Integer lanThi;
    private LocalDate ngayThi;
    private String caThi;
    private String gioBatDau;
    private String phongThi;
    private String soBaoDanh;
    private String trangThaiDuThi;
    private String lyDo;
}
