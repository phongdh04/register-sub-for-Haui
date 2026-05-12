package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LopResponse {
    private Long idLop;
    private String maLop;
    private String tenLop;
    private Integer namNhapHoc;
    private Long idNganh;
    private String tenNganh;
}
