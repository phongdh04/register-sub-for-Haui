package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SinhVienRequest {
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Size(max = 20)
    private String maSinhVien;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 200)
    private String hoTen;

    @NotNull(message = "ID Lớp không được để trống")
    private Long idLop;

    private Long idCoVan;
}
