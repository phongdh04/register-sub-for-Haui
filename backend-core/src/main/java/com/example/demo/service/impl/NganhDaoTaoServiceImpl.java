package com.example.demo.service.impl;

import com.example.demo.domain.entity.Khoa;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.payload.request.NganhDaoTaoRequest;
import com.example.demo.payload.response.NganhDaoTaoResponse;
import com.example.demo.repository.KhoaRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.service.INganhDaoTaoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ xử lý nghiệp vụ Ngành Đào Tạo.
 * DIP: Phụ thuộc vào NganhDaoTaoRepository và KhoaRepository qua abstraction.
 */
@Service
@RequiredArgsConstructor
public class NganhDaoTaoServiceImpl implements INganhDaoTaoService {

    private final NganhDaoTaoRepository nganhRepository;
    private final KhoaRepository khoaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NganhDaoTaoResponse> getAll() {
        return nganhRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NganhDaoTaoResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NganhDaoTaoResponse> getByKhoa(Long idKhoa) {
        return nganhRepository.findByKhoa_IdKhoa(idKhoa)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NganhDaoTaoResponse create(NganhDaoTaoRequest request) {
        if (nganhRepository.existsByMaNganh(request.getMaNganh())) {
            throw new IllegalArgumentException("Mã ngành đã tồn tại: " + request.getMaNganh());
        }
        Khoa khoa = khoaRepository.findById(request.getIdKhoa())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa ID: " + request.getIdKhoa()));

        NganhDaoTao nganh = NganhDaoTao.builder()
                .maNganh(request.getMaNganh())
                .tenNganh(request.getTenNganh())
                .heDaoTao(request.getHeDaoTao())
                .khoa(khoa)
                .build();
        return toResponse(nganhRepository.save(nganh));
    }

    @Override
    @Transactional
    public NganhDaoTaoResponse update(Long id, NganhDaoTaoRequest request) {
        NganhDaoTao nganh = findOrThrow(id);
        Khoa khoa = khoaRepository.findById(request.getIdKhoa())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa ID: " + request.getIdKhoa()));
        nganh.setTenNganh(request.getTenNganh());
        nganh.setHeDaoTao(request.getHeDaoTao());
        nganh.setKhoa(khoa);
        return toResponse(nganhRepository.save(nganh));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        nganhRepository.deleteById(id);
    }

    private NganhDaoTao findOrThrow(Long id) {
        return nganhRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ngành đào tạo ID: " + id));
    }

    private NganhDaoTaoResponse toResponse(NganhDaoTao n) {
        return NganhDaoTaoResponse.builder()
                .idNganh(n.getIdNganh())
                .maNganh(n.getMaNganh())
                .tenNganh(n.getTenNganh())
                .heDaoTao(n.getHeDaoTao())
                .idKhoa(n.getKhoa() != null ? n.getKhoa().getIdKhoa() : null)
                .tenKhoa(n.getKhoa() != null ? n.getKhoa().getTenKhoa() : null)
                .build();
    }
}
