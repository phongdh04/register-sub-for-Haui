package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NganhDaoTaoRequest {
    @NotBlank(message = "Mã ngành không được để trống")
    @Size(max = 20)
    private String maNganh;

    @NotBlank(message = "Tên ngành không được để trống")
    @Size(max = 200)
    private String tenNganh;

    @Size(max = 50)
    private String heDaoTao; // Đại Trà, CLC, Tài Năng

    @NotNull(message = "ID Khoa không được để trống")
    private Long idKhoa;
}
