package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CtdtHocPhanRequest {
    @NotNull(message = "ID CTĐT không được để trống")
    private Long idCtdt;

    @NotNull(message = "ID Học phần không được để trống")
    private Long idHocPhan;

    @NotBlank(message = "Khối kiến thức không được để trống")
    @Size(max = 40)
    private String khoiKienThuc;

    private Boolean batBuoc = true;
    private Integer hocKyGoiY;
}
