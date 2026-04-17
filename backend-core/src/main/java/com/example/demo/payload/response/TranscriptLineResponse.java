package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TranscriptLineResponse {
    private Long idDangKy;
    private String maLopHp;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private BigDecimal diemHe4;
    private String diemChu;
    /** Có điểm hệ 4 và đã công bố chính thức (tính GPA / tín chỉ có điểm). */
    private boolean daCoDiem;
    /** false = điểm nháp (CHO_CONG_BO), vẫn có thể hiển thị điểm tạm. */
    private boolean congBoChinhThuc;
}
