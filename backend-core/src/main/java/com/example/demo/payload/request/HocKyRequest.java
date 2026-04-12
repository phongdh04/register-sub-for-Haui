package com.example.demo.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HocKyRequest {
    @NotBlank(message = "Năm học không được để trống")
    private String namHoc; // "2024-2025"

    @NotNull
    @Min(1) @Max(3)
    private Integer kyThu;

    private Boolean trangThaiHienHanh = false;
}
