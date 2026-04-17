package com.example.demo.service.impl;

import com.example.demo.domain.entity.NhatKyHanhDong;
import com.example.demo.payload.response.AuditLogRowResponse;
import com.example.demo.repository.NhatKyHanhDongRepository;
import com.example.demo.service.IAuditTrailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditTrailServiceImpl implements IAuditTrailService {

    private final NhatKyHanhDongRepository nhatKyHanhDongRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void record(String tenDangNhap, String vaiTro, String maHanhDong, String moTaNgan, Map<String, ?> chiTiet) {
        String json = null;
        if (chiTiet != null && !chiTiet.isEmpty()) {
            try {
                json = objectMapper.writeValueAsString(chiTiet);
            } catch (JsonProcessingException e) {
                json = "{\"error\":\"json\"}";
            }
        }
        if (json != null && json.length() > 4000) {
            json = json.substring(0, 3997) + "...";
        }
        nhatKyHanhDongRepository.save(NhatKyHanhDong.builder()
                .thoiGian(LocalDateTime.now())
                .tenDangNhap(tenDangNhap != null ? tenDangNhap : "unknown")
                .vaiTro(vaiTro)
                .maHanhDong(maHanhDong)
                .moTaNgan(moTaNgan)
                .chiTietJson(json)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogRowResponse> pageLogs(int page, int size, String maHanhDong) {
        String ma = (maHanhDong == null || maHanhDong.isBlank()) ? null : maHanhDong.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return nhatKyHanhDongRepository.pageByOptionalAction(ma, pageable)
                .map(n -> AuditLogRowResponse.builder()
                        .idNhatKy(n.getIdNhatKy())
                        .thoiGian(n.getThoiGian())
                        .tenDangNhap(n.getTenDangNhap())
                        .vaiTro(n.getVaiTro())
                        .maHanhDong(n.getMaHanhDong())
                        .moTaNgan(n.getMoTaNgan())
                        .chiTietJson(n.getChiTietJson())
                        .build());
    }
}
