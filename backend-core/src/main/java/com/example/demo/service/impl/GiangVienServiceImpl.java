package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.Khoa;
import com.example.demo.payload.request.GiangVienRequest;
import com.example.demo.payload.response.GiangVienResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.KhoaRepository;
import com.example.demo.service.IGiangVienService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ xử lý nghiệp vụ Giảng Viên.
 * DIP: Phụ thuộc vào GiangVienRepository và KhoaRepository qua abstraction.
 */
@Service
@RequiredArgsConstructor
public class GiangVienServiceImpl implements IGiangVienService {

    private final GiangVienRepository giangVienRepository;
    private final KhoaRepository khoaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GiangVienResponse> getAll() {
        return giangVienRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiangVienResponse> getAllPaged(Pageable pageable) {
        return giangVienRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GiangVienResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiangVienResponse> getByKhoa(Long idKhoa) {
        return giangVienRepository.findByKhoa_IdKhoa(idKhoa)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiangVienResponse> getByKhoaPaged(Long idKhoa, Pageable pageable) {
        return giangVienRepository.findByKhoa_IdKhoa(idKhoa, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public GiangVienResponse create(GiangVienRequest request) {
        if (giangVienRepository.existsByMaGiangVien(request.getMaGiangVien())) {
            throw new IllegalArgumentException("Mã giảng viên đã tồn tại: " + request.getMaGiangVien());
        }
        Khoa khoa = khoaRepository.findById(request.getIdKhoa())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa với ID: " + request.getIdKhoa()));

        GiangVien gv = GiangVien.builder()
                .maGiangVien(request.getMaGiangVien())
                .tenGiangVien(request.getTenGiangVien())
                .email(request.getEmail())
                .sdt(request.getSdt())
                .hocHamHocVi(request.getHocHamHocVi())
                .khoa(khoa)
                .build();
        return toResponse(giangVienRepository.save(gv));
    }

    @Override
    @Transactional
    public GiangVienResponse update(Long id, GiangVienRequest request) {
        GiangVien gv = findOrThrow(id);
        Khoa khoa = khoaRepository.findById(request.getIdKhoa())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa với ID: " + request.getIdKhoa()));
        gv.setTenGiangVien(request.getTenGiangVien());
        gv.setEmail(request.getEmail());
        gv.setSdt(request.getSdt());
        gv.setHocHamHocVi(request.getHocHamHocVi());
        gv.setKhoa(khoa);
        return toResponse(giangVienRepository.save(gv));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        giangVienRepository.deleteById(id);
    }

    // --- Private helpers ---

    private GiangVien findOrThrow(Long id) {
        return giangVienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên với ID: " + id));
    }

    private GiangVienResponse toResponse(GiangVien gv) {
        return GiangVienResponse.builder()
                .idGiangVien(gv.getIdGiangVien())
                .maGiangVien(gv.getMaGiangVien())
                .tenGiangVien(gv.getTenGiangVien())
                .email(gv.getEmail())
                .sdt(gv.getSdt())
                .hocHamHocVi(gv.getHocHamHocVi())
                .idKhoa(gv.getKhoa() != null ? gv.getKhoa().getIdKhoa() : null)
                .tenKhoa(gv.getKhoa() != null ? gv.getKhoa().getTenKhoa() : null)
                .build();
    }
}
