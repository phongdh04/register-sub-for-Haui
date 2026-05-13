package com.example.demo.payload.request;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaiKhoanRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;

    private String password;

    @NotNull(message = "Role không được để trống")
    private Role role;

    private Status trangThai;

    private Long sinhVienId;

    private Long giangVienId;

    private String emailOtp;

    private Boolean mfaBat;
}
