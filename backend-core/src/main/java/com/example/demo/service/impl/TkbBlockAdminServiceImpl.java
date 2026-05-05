package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.TkbBlock;
import com.example.demo.payload.request.TkbBlockUpsertRequest;
import com.example.demo.payload.response.TkbBlockResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.TkbBlockRepository;
import com.example.demo.service.ITkbBlockAdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TkbBlockAdminServiceImpl implements ITkbBlockAdminService {

    private final TkbBlockRepository tkbBlockRepository;
    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TkbBlockResponse> listByHocKy(Long hocKyId) {
        return tkbBlockRepository.findByHocKy_IdHocKy(hocKyId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public TkbBlockResponse create(Long hocKyId, TkbBlockUpsertRequest request) {
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        if (tkbBlockRepository.existsByHocKy_IdHocKyAndMaBlock(hocKyId, request.getMaBlock())) {
            throw new IllegalArgumentException("Mã block đã tồn tại trong học kỳ: " + request.getMaBlock());
        }
        validateDanhSachHocPhan(hocKyId, request.getDanhSachIdHocPhan());
        TkbBlock b = TkbBlock.builder()
                .hocKy(hk)
                .maBlock(norm(request.getMaBlock()))
                .tenBlock(norm(request.getTenBlock()))
                .jsonSlots(copySlots(request.getJsonSlots()))
                .danhSachIdHocPhanJson(normalizeIds(request.getDanhSachIdHocPhan()))
                .batBuocChonCaBlock(Boolean.TRUE.equals(request.getBatBuocChonCaBlock()))
                .build();
        return toResponse(tkbBlockRepository.save(b));
    }

    @Override
    @Transactional
    public TkbBlockResponse update(Long hocKyId, Long idTkbBlock, TkbBlockUpsertRequest request) {
        TkbBlock b = tkbBlockRepository.findById(idTkbBlock)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy TkbBlock: " + idTkbBlock));
        if (!Objects.equals(b.getHocKy().getIdHocKy(), hocKyId)) {
            throw new IllegalArgumentException("Block không thuộc học kỳ path");
        }
        if (!Objects.equals(b.getMaBlock(), request.getMaBlock())
                && tkbBlockRepository.existsByHocKy_IdHocKyAndMaBlock(hocKyId, request.getMaBlock())) {
            throw new IllegalArgumentException("Mã block đã tồn tại trong học kỳ: " + request.getMaBlock());
        }
        validateDanhSachHocPhan(hocKyId, request.getDanhSachIdHocPhan());
        b.setMaBlock(norm(request.getMaBlock()));
        b.setTenBlock(norm(request.getTenBlock()));
        b.setJsonSlots(copySlots(request.getJsonSlots()));
        b.setDanhSachIdHocPhanJson(normalizeIds(request.getDanhSachIdHocPhan()));
        b.setBatBuocChonCaBlock(Boolean.TRUE.equals(request.getBatBuocChonCaBlock()));
        return toResponse(tkbBlockRepository.save(b));
    }

    @Override
    @Transactional
    public void delete(Long hocKyId, Long idTkbBlock) {
        TkbBlock b = tkbBlockRepository.findById(idTkbBlock)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy TkbBlock: " + idTkbBlock));
        if (!Objects.equals(b.getHocKy().getIdHocKy(), hocKyId)) {
            throw new IllegalArgumentException("Block không thuộc học kỳ path");
        }
        tkbBlockRepository.delete(b);
    }

    private void validateDanhSachHocPhan(Long hocKyId, List<Long> ids) {
        List<Long> normalized = normalizeIds(ids);
        if (normalized.isEmpty()) {
            return;
        }
        List<LopHocPhan> lhps = lopHocPhanRepository.findByHocKy_IdHocKy(hocKyId);
        Set<Long> available = new LinkedHashSet<>();
        for (LopHocPhan l : lhps) {
            if (l.getHocPhan() != null && l.getHocPhan().getIdHocPhan() != null) {
                available.add(l.getHocPhan().getIdHocPhan());
            }
        }
        for (Long id : normalized) {
            if (!available.contains(id)) {
                throw new IllegalArgumentException("id_hoc_phan không thuộc học kỳ hoặc chưa mở lớp: " + id);
            }
        }
    }

    private static String norm(String s) {
        return s == null ? null : s.trim();
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).distinct().sorted().toList();
    }

    private static List<Map<String, Object>> copySlots(List<Map<String, Object>> slots) {
        if (slots == null) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>(slots.size());
        for (Map<String, Object> s : slots) {
            out.add(s == null ? Map.of() : new java.util.LinkedHashMap<>(s));
        }
        return out;
    }

    private TkbBlockResponse toResponse(TkbBlock b) {
        return TkbBlockResponse.builder()
                .idTkbBlock(b.getIdTkbBlock())
                .idHocKy(b.getHocKy() != null ? b.getHocKy().getIdHocKy() : null)
                .maBlock(b.getMaBlock())
                .tenBlock(b.getTenBlock())
                .jsonSlots(b.getJsonSlots())
                .danhSachIdHocPhan(b.getDanhSachIdHocPhanJson())
                .batBuocChonCaBlock(b.getBatBuocChonCaBlock())
                .build();
    }
}
