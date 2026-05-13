package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.payload.request.HocKyLichDangKyRequest;
import com.example.demo.payload.request.HocKyRequest;
import com.example.demo.payload.response.HocKyResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.service.IHocKyService;
import com.example.demo.support.RegistrationScheduleChecker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ xử lý nghiệp vụ Học Kỳ.
 * Đảm bảo duy nhất 1 Học Kỳ được đặt trang_thai_hien_hanh = true.
 */
@Service
@RequiredArgsConstructor
public class HocKyServiceImpl implements IHocKyService {

    private final HocKyRepository hocKyRepository;
    private final RegistrationScheduleChecker registrationScheduleChecker;

    @Override
    @Transactional(readOnly = true)
    public List<HocKyResponse> getAll() {
        return hocKyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HocKyResponse> getAllPaged(Pageable pageable) {
        return hocKyRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HocKyResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public HocKyResponse getCurrent() {
        HocKy hocKy = hocKyRepository.findByTrangThaiHienHanhTrue()
                .orElseThrow(() -> new EntityNotFoundException("Chưa có học kỳ hiện hành nào được kích hoạt"));
        return toResponse(hocKy);
    }

    @Override
    @Transactional
    public HocKyResponse create(HocKyRequest request) {
        if (hocKyRepository.existsByNamHocAndKyThu(request.getNamHoc(), request.getKyThu())) {
            throw new IllegalArgumentException(
                    "Học kỳ " + request.getKyThu() + " năm " + request.getNamHoc() + " đã tồn tại");
        }
        HocKy hocKy = HocKy.builder()
                .namHoc(request.getNamHoc())
                .kyThu(request.getKyThu())
                .trangThaiHienHanh(false)
                .build();
        return toResponse(hocKyRepository.save(hocKy));
    }

    /**
     * Đặt học kỳ này là học kỳ hiện hành.
     * OCP: Tắt học kỳ cũ trước khi bật cái mới - không cần sửa logic create().
     */
    @Override
    @Transactional
    public HocKyResponse setActive(Long id) {
        // Tắt tất cả học kỳ đang active
        hocKyRepository.findByTrangThaiHienHanhTrue().ifPresent(current -> {
            current.setTrangThaiHienHanh(false);
            hocKyRepository.save(current);
        });
        // Kích hoạt học kỳ mới
        HocKy hocKy = findOrThrow(id);
        hocKy.setTrangThaiHienHanh(true);
        return toResponse(hocKyRepository.save(hocKy));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        hocKyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HocKyResponse updateLichDangKy(Long id, HocKyLichDangKyRequest request) {
        HocKy hk = findOrThrow(id);
        validateWindowPair("Đăng ký trước", request.getPreDangKyMoTu(), request.getPreDangKyMoDen());
        validateWindowPair("Đăng ký chính thức", request.getDangKyChinhThucTu(), request.getDangKyChinhThucDen());

        hk.setPreDangKyMoTu(request.getPreDangKyMoTu());
        hk.setPreDangKyMoDen(request.getPreDangKyMoDen());
        hk.setDangKyChinhThucTu(request.getDangKyChinhThucTu());
        hk.setDangKyChinhThucDen(request.getDangKyChinhThucDen());

        return toResponse(hocKyRepository.save(hk));
    }

    private static void validateWindowPair(String label, Instant from, Instant to) {
        boolean hasFrom = from != null;
        boolean hasTo = to != null;
        if (hasFrom != hasTo) {
            throw new IllegalArgumentException(label + ": phải nhập đủ mốc bắt đầu và kết thúc, hoặc để trống cả hai.");
        }
        if (hasFrom && from.isAfter(to)) {
            throw new IllegalArgumentException(label + ": mốc bắt đầu phải trước hoặc trùng mốc kết thúc.");
        }
    }

    // --- Private helpers ---

    private HocKy findOrThrow(Long id) {
        return hocKyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ với ID: " + id));
    }

    private HocKyResponse toResponse(HocKy hocKy) {
        return HocKyResponse.builder()
                .idHocKy(hocKy.getIdHocKy())
                .namHoc(hocKy.getNamHoc())
                .kyThu(hocKy.getKyThu())
                .trangThaiHienHanh(hocKy.getTrangThaiHienHanh())
                .tenHocKy("Học kỳ " + hocKy.getKyThu() + " năm " + hocKy.getNamHoc())
                .preDangKyMoTu(hocKy.getPreDangKyMoTu())
                .preDangKyMoDen(hocKy.getPreDangKyMoDen())
                .dangKyChinhThucTu(hocKy.getDangKyChinhThucTu())
                .dangKyChinhThucDen(hocKy.getDangKyChinhThucDen())
                .preDangKyDangMo(registrationScheduleChecker.isPreRegistrationOpen(hocKy))
                .dangKyChinhThucDangMo(registrationScheduleChecker.isOfficialRegistrationOpen(hocKy))
                .build();
    }
}
