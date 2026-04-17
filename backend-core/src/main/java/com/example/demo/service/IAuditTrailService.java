package com.example.demo.service;

import com.example.demo.payload.response.AuditLogRowResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface IAuditTrailService {

    void record(String tenDangNhap, String vaiTro, String maHanhDong, String moTaNgan, Map<String, ?> chiTiet);

    Page<AuditLogRowResponse> pageLogs(int page, int size, String maHanhDong);
}
