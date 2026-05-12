package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LopRequest {
    @NotBlank(message = "Mã lớp không được để trống")
    @Size(max = 20)
    private String maLop;

    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 100)
    private String tenLop;

    private Integer namNhapHoc;

    @NotNull(message = "ID Ngành không được để trống")
    private Long idNganh;
}
