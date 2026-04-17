package com.example.demo.payload.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LecturerUpdateGradeRequest {

    /** Điểm hệ 4 (0.00 – 4.00). */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("4.0")
    private BigDecimal diemHe4;
}
