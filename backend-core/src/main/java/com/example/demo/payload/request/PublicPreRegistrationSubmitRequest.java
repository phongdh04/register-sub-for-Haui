package com.example.demo.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PublicPreRegistrationSubmitRequest {

    @NotBlank
    @Size(max = 200)
    private String hoTen;

    @NotBlank
    @Size(max = 20)
    private String ngaySinh;

    @Size(max = 20)
    private String gioiTinh;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String soDienThoai;

    @Pattern(regexp = "^[0-9]{9,20}$", message = "soCCCD phải là chuỗi số 9-20 ký tự")
    private String soCCCD;

    @Size(max = 500)
    private String diaChiThuongTru;

    @Size(max = 100)
    private String tinhThanh;

    @NotBlank
    @Size(max = 50)
    private String maNganhDangKy;

    @NotBlank
    @Size(max = 20)
    private String nienKhoa;

    @Size(max = 50)
    private String coSo;

    @Size(max = 100)
    private String nguonTuyenSinh;
}
