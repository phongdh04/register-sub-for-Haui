package com.example.demo.service.impl;

import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.payload.request.LopRequest;
import com.example.demo.payload.response.LopResponse;
import com.example.demo.repository.LopRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.service.ILopService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LopServiceImpl implements ILopService {

    private final LopRepository lopRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LopResponse> getAll() {
        return lopRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LopResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public LopResponse create(LopRequest request) {
        if (lopRepository.existsByMaLop(request.getMaLop())) {
            throw new IllegalArgumentException("Mã lớp đã tồn tại: " + request.getMaLop());
        }
        NganhDaoTao nganh = nganhDaoTaoRepository.findById(request.getIdNganh())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Ngành: " + request.getIdNganh()));
        Lop lop = Lop.builder()
                .maLop(request.getMaLop())
                .tenLop(request.getTenLop())
                .namNhapHoc(request.getNamNhapHoc())
                .nganhDaoTao(nganh)
                .build();
        return toResponse(lopRepository.save(lop));
    }

    @Override
    @Transactional
    public LopResponse update(Long id, LopRequest request) {
        Lop lop = findOrThrow(id);
        lop.setTenLop(request.getTenLop());
        lop.setNamNhapHoc(request.getNamNhapHoc());
        if (request.getIdNganh() != null) {
            lop.setNganhDaoTao(nganhDaoTaoRepository.findById(request.getIdNganh())
                    .orElseThrow(() -> new EntityNotFoundException("Ngành không tồn tại")));
        }
        return toResponse(lopRepository.save(lop));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        lopRepository.deleteById(id);
    }

    private Lop findOrThrow(Long id) {
        return lopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Lớp: " + id));
    }

    private LopResponse toResponse(Lop lop) {
        return LopResponse.builder()
                .idLop(lop.getIdLop())
                .maLop(lop.getMaLop())
                .tenLop(lop.getTenLop())
                .namNhapHoc(lop.getNamNhapHoc())
                .idNganh(lop.getNganhDaoTao().getIdNganh())
                .tenNganh(lop.getNganhDaoTao().getTenNganh())
                .build();
    }
}
