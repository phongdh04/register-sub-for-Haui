package com.example.demo.payload.response;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaiKhoanResponse {
    private Long id;
    private String username;
    private Role role;
    private Status trangThai;
    private Boolean mfaBat;
    private String emailOtp;
    private Long sinhVienId;
    private String maSinhVien;
    private String hoTenSinhVien;
    private Long giangVienId;
    private String maGiangVien;
    private String tenGiangVien;
}
