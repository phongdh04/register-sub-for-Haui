package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NganhDaoTaoResponse {
    private Long idNganh;
    private String maNganh;
    private String tenNganh;
    private String heDaoTao;
    private Long idKhoa;
    private String tenKhoa;
}
