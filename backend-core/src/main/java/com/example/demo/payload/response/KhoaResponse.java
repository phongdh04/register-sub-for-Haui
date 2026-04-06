package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO Response trả về cho client.
 * SRP: Chỉ là data carrier, không có logic nghiệp vụ.
 * Tránh expose entity JPA trực tiếp ra ngoài API (bảo mật, decoupling).
 */
@Data
@Builder
public class KhoaResponse {
    private Long idKhoa;
    private String maKhoa;
    private String tenKhoa;
    private String moTa;
}
