package com.example.demo.service.impl;

import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.payload.request.SinhVienRequest;
import com.example.demo.payload.response.SinhVienResponse;
import com.example.demo.repository.LopRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.service.ISinhVienAdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SinhVienAdminServiceImpl implements ISinhVienAdminService {

    private final SinhVienRepository sinhVienRepository;
    private final LopRepository lopRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SinhVienResponse> getAll() {
        return sinhVienRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SinhVienResponse> getAllPaged(Pageable pageable) {
        return sinhVienRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SinhVienResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public SinhVienResponse create(SinhVienRequest request) {
        if (sinhVienRepository.existsByMaSinhVien(request.getMaSinhVien())) {
            throw new IllegalArgumentException("MSSV đã tồn tại: " + request.getMaSinhVien());
        }
        Lop lop = lopRepository.findById(request.getIdLop())
                .orElseThrow(() -> new EntityNotFoundException("Lớp không tồn tại: " + request.getIdLop()));
        SinhVien sv = SinhVien.builder()
                .maSinhVien(request.getMaSinhVien())
                .hoTen(request.getHoTen())
                .lop(lop)
                .build();
        return toResponse(sinhVienRepository.save(sv));
    }

    @Override
    @Transactional
    public SinhVienResponse update(Long id, SinhVienRequest request) {
        SinhVien sv = findOrThrow(id);
        sv.setHoTen(request.getHoTen());
        if (request.getIdLop() != null) {
            sv.setLop(lopRepository.findById(request.getIdLop())
                    .orElseThrow(() -> new EntityNotFoundException("Lớp không tồn tại")));
        }
        return toResponse(sinhVienRepository.save(sv));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        sinhVienRepository.deleteById(id);
    }

    private SinhVien findOrThrow(Long id) {
        return sinhVienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy SV: " + id));
    }

    private SinhVienResponse toResponse(SinhVien sv) {
        Lop lop = sv.getLop();
        return SinhVienResponse.builder()
                .idSinhVien(sv.getIdSinhVien())
                .maSinhVien(sv.getMaSinhVien())
                .hoTen(sv.getHoTen())
                .idLop(lop != null ? lop.getIdLop() : null)
                .maLop(lop != null ? lop.getMaLop() : null)
                .tenLop(lop != null ? lop.getTenLop() : null)
                .idNganh(lop != null && lop.getNganhDaoTao() != null ? lop.getNganhDaoTao().getIdNganh() : null)
                .tenNganh(lop != null && lop.getNganhDaoTao() != null ? lop.getNganhDaoTao().getTenNganh() : null)
                .namNhapHoc(lop != null ? lop.getNamNhapHoc() : null)
                .build();
    }
}
