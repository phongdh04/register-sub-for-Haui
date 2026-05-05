package com.example.demo.payload.response;

import com.example.demo.domain.enums.LoaiPhong;
import com.example.demo.domain.enums.TrangThaiPhong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongHocResponse {

    private Long idPhong;
    private String maPhong;
    private String tenPhong;
    private String maCoSo;
    private LoaiPhong loaiPhong;
    private Integer sucChua;
    private TrangThaiPhong trangThai;
    private String ghiChu;
}
