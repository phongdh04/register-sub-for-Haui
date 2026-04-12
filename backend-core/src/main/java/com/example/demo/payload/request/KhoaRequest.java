package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO Request tạo/cập nhật Khoa.
 * SRP: Chỉ chứa dữ liệu validate từ client, không chứa logic nghiệp vụ.
 */
@Data
public class KhoaRequest {
    @NotBlank(message = "Mã Khoa không được để trống")
    @Size(max = 20)
    private String maKhoa;

    @NotBlank(message = "Tên Khoa không được để trống")
    @Size(max = 200)
    private String tenKhoa;

    private String moTa;
}
