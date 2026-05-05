package com.example.demo.service.impl;

import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.enums.TrangThaiPhong;
import com.example.demo.payload.request.PhongHocUpsertRequest;
import com.example.demo.payload.response.PhongHocResponse;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.service.IPhongHocService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PhongHocServiceImpl implements IPhongHocService {

    private final PhongHocRepository phongHocRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PhongHocResponse> page(Pageable pageable, String maCoSo) {
        Page<PhongHoc> page;
        if (maCoSo != null && !maCoSo.isBlank()) {
            page = phongHocRepository.findByMaCoSoIgnoreCase(maCoSo.trim(), pageable);
        } else {
            page = phongHocRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PhongHocResponse getById(Long id) {
        return phongHocRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng: " + id));
    }

    @Override
    @Transactional
    public PhongHocResponse create(PhongHocUpsertRequest request) {
        String ma = request.getMaPhong().trim();
        if (phongHocRepository.existsByMaPhong(ma)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã phòng đã tồn tại: " + ma);
        }
        PhongHoc e = PhongHoc.builder()
                .maPhong(ma)
                .tenPhong(request.getTenPhong().trim())
                .maCoSo(request.getMaCoSo().trim())
                .loaiPhong(request.getLoaiPhong())
                .sucChua(request.getSucChua())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : TrangThaiPhong.HOAT_DONG)
                .ghiChu(request.getGhiChu())
                .build();
        return toResponse(phongHocRepository.save(e));
    }

    @Override
    @Transactional
    public PhongHocResponse update(Long id, PhongHocUpsertRequest request) {
        PhongHoc e = phongHocRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng: " + id));
        String ma = request.getMaPhong().trim();
        if (!ma.equalsIgnoreCase(e.getMaPhong()) && phongHocRepository.existsByMaPhong(ma)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã phòng đã tồn tại: " + ma);
        }
        e.setMaPhong(ma);
        e.setTenPhong(request.getTenPhong().trim());
        e.setMaCoSo(request.getMaCoSo().trim());
        e.setLoaiPhong(request.getLoaiPhong());
        e.setSucChua(request.getSucChua());
        if (request.getTrangThai() != null) {
            e.setTrangThai(request.getTrangThai());
        }
        e.setGhiChu(request.getGhiChu());
        return toResponse(phongHocRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!phongHocRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy phòng: " + id);
        }
        if (lopHocPhanRepository.existsByPhongHoc_IdPhong(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Không xóa được: đang có lớp học phần gắn phòng này.");
        }
        phongHocRepository.deleteById(id);
    }

    private PhongHocResponse toResponse(PhongHoc e) {
        return PhongHocResponse.builder()
                .idPhong(e.getIdPhong())
                .maPhong(e.getMaPhong())
                .tenPhong(e.getTenPhong())
                .maCoSo(e.getMaCoSo())
                .loaiPhong(e.getLoaiPhong())
                .sucChua(e.getSucChua())
                .trangThai(e.getTrangThai())
                .ghiChu(e.getGhiChu())
                .build();
    }
}
