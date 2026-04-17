package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LecturerRetakeAppealDecisionRequest {

    /**
     * DONG_Y: cập nhật điểm theo phúc khảo; TU_CHOI: từ chối yêu cầu.
     */
    @NotBlank
    @Pattern(regexp = "DONG_Y|TU_CHOI")
    private String quyetDinh;

    private String ghiChuGiangVien;

    /** Bắt buộc khi quyetDinh = DONG_Y */
    private BigDecimal diemSauPhucKhao;
}
