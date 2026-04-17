package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileProcedureResponse {
    private String ma;
    private String ten;
    private String trangThai;
    private String ghiChu;
}
