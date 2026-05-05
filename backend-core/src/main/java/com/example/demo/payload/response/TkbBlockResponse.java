package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TkbBlockResponse {
    private Long idTkbBlock;
    private Long idHocKy;
    private String maBlock;
    private String tenBlock;
    private List<Map<String, Object>> jsonSlots;
    private List<Long> danhSachIdHocPhan;
    private Boolean batBuocChonCaBlock;
}
