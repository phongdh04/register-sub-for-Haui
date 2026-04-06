package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GiangVienRequest {
    @NotBlank(message = "Mã giảng viên không được để trống")
    @Size(max = 20)
    private String maGiangVien;

    @NotBlank(message = "Tên giảng viên không được để trống")
    @Size(max = 200)
    private String tenGiangVien;

    private String email;
    private String sdt;
    private String hocHamHocVi; // PGS.TS, ThS...

    @NotNull(message = "ID Khoa không được để trống")
    private Long idKhoa;
}
