package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingSnapshotRowResponse {
    private Long idLopHp;
    private String maLopHp;
    private Long idHocPhan;
    private String tenHocPhan;
    private Long idGiangVien;
    private String tenGiangVien;
    private Long idPhongHoc;
    private String maPhongHoc;

    private Long idTkbBlock;
    private String maTkbBlock;

    private List<Map<String, Object>> thoiKhoaBieuJson;
    private String trangThai;
}
