package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocPhan;
import com.example.demo.payload.request.HocPhanRequest;
import com.example.demo.payload.response.HocPhanResponse;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.service.IHocPhanService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ xử lý nghiệp vụ Học Phần (Master Course Data).
 * OCP: Khi cần thêm logic validate phức tạp (vd: check xem môn có đang được dạy),
 *      chỉ cần thêm method hoặc tạo Strategy mới, không sửa create().
 */
@Service
@RequiredArgsConstructor
public class HocPhanServiceImpl implements IHocPhanService {

    private final HocPhanRepository hocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HocPhanResponse> getAll() {
        return hocPhanRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HocPhanResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public HocPhanResponse getByMa(String maHocPhan) {
        HocPhan hp = hocPhanRepository.findByMaHocPhan(maHocPhan)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học phần: " + maHocPhan));
        return toResponse(hp);
    }

    @Override
    @Transactional
    public HocPhanResponse create(HocPhanRequest request) {
        if (hocPhanRepository.existsByMaHocPhan(request.getMaHocPhan())) {
            throw new IllegalArgumentException("Mã học phần đã tồn tại: " + request.getMaHocPhan());
        }
        // Builder Pattern: Tạo đối tượng sạch sẽ, khớp cả JSONB field
        HocPhan hp = HocPhan.builder()
                .maHocPhan(request.getMaHocPhan())
                .tenHocPhan(request.getTenHocPhan())
                .maIn(request.getMaIn())
                .soTinChi(request.getSoTinChi())
                .loaiMon(request.getLoaiMon())
                .thuocTinhJson(request.getThuocTinhJson())
                .dieuKienRangBuocJson(request.getDieuKienRangBuocJson())
                .build();
        return toResponse(hocPhanRepository.save(hp));
    }

    @Override
    @Transactional
    public HocPhanResponse update(Long id, HocPhanRequest request) {
        HocPhan hp = findOrThrow(id);
        hp.setTenHocPhan(request.getTenHocPhan());
        hp.setMaIn(request.getMaIn());
        hp.setSoTinChi(request.getSoTinChi());
        hp.setLoaiMon(request.getLoaiMon());
        hp.setThuocTinhJson(request.getThuocTinhJson());
        hp.setDieuKienRangBuocJson(request.getDieuKienRangBuocJson());
        return toResponse(hocPhanRepository.save(hp));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        hocPhanRepository.deleteById(id);
    }

    // --- Private helpers ---

    private HocPhan findOrThrow(Long id) {
        return hocPhanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học phần với ID: " + id));
    }

    private HocPhanResponse toResponse(HocPhan hp) {
        return HocPhanResponse.builder()
                .idHocPhan(hp.getIdHocPhan())
                .maHocPhan(hp.getMaHocPhan())
                .tenHocPhan(hp.getTenHocPhan())
                .maIn(hp.getMaIn())
                .soTinChi(hp.getSoTinChi())
                .loaiMon(hp.getLoaiMon())
                .thuocTinhJson(hp.getThuocTinhJson())
                .dieuKienRangBuocJson(hp.getDieuKienRangBuocJson())
                .build();
    }
}
