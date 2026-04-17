package com.example.demo.payload.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTuitionQrRequest {

    @NotNull
    @DecimalMin(value = "1000.0", message = "Số tiền tối thiểu 1000 VND")
    private BigDecimal soTien;

    /**
     * MOCK (mặc định) | VNPAY | MOMO
     */
    @Size(max = 20)
    private String provider;

    @Size(max = 500)
    private String noiDung;
}
