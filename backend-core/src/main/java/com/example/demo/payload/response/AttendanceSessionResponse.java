package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AttendanceSessionResponse {
    private Long idBuoi;
    private Long idLopHp;
    private String maLopHp;
    private String tenHocPhan;
    private LocalDate ngayBuoi;
    private String publicToken;
    /** Gợi ý URL ảnh QR (api.qrserver.com) — client có thể tự dựng khác. */
    private String qrImageUrl;
    private int tongSo;
    private int coMat;
    private int vang;
    private int phep;
    private List<AttendanceRowResponse> rows;
}
