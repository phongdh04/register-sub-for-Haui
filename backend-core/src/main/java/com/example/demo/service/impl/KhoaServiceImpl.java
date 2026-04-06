package com.example.demo.service.impl;

import com.example.demo.domain.entity.Khoa;
import com.example.demo.payload.request.KhoaRequest;
import com.example.demo.payload.response.KhoaResponse;
import com.example.demo.repository.KhoaRepository;
import com.example.demo.service.IKhoaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Service chỉ xử lý nghiệp vụ Khoa, không kiêm logic ở tầng khác.
 * DIP: Phụ thuộc vào KhoaRepository (abstraction/interface), không phụ thuộc trực tiếp vào implementation.
 * OCP: Mở rộng logic bằng cách thêm method mới, không sửa method cũ.
 */
@Service
@RequiredArgsConstructor
public class KhoaServiceImpl implements IKhoaService {

    // DIP: Inject qua constructor (không dùng @Autowired field để dễ test)
    private final KhoaRepository khoaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KhoaResponse> getAll() {
        return khoaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KhoaResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public KhoaResponse create(KhoaRequest request) {
        if (khoaRepository.existsByMaKhoa(request.getMaKhoa())) {
            throw new IllegalArgumentException("Mã Khoa đã tồn tại: " + request.getMaKhoa());
        }
        // Builder Pattern (Lombok) - tạo đối tượng sạch sẽ, không dùng setter rải rác
        Khoa khoa = Khoa.builder()
                .maKhoa(request.getMaKhoa())
                .tenKhoa(request.getTenKhoa())
                .moTa(request.getMoTa())
                .build();
        return toResponse(khoaRepository.save(khoa));
    }

    @Override
    @Transactional
    public KhoaResponse update(Long id, KhoaRequest request) {
        Khoa khoa = findOrThrow(id);
        khoa.setTenKhoa(request.getTenKhoa());
        khoa.setMoTa(request.getMoTa());
        // Không cho phép đổi maKhoa (khóa tự nhiên)
        return toResponse(khoaRepository.save(khoa));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        khoaRepository.deleteById(id);
    }

    // --- Private helpers ---

    private Khoa findOrThrow(Long id) {
        return khoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa với ID: " + id));
    }

    private KhoaResponse toResponse(Khoa khoa) {
        return KhoaResponse.builder()
                .idKhoa(khoa.getIdKhoa())
                .maKhoa(khoa.getMaKhoa())
                .tenKhoa(khoa.getTenKhoa())
                .moTa(khoa.getMoTa())
                .build();
    }
}
