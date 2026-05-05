package com.example.demo.service.impl;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.service.LopHocPhongDualWriteService;
import com.example.demo.service.support.PhongStringResolver;
import com.example.demo.service.support.PhongStringResolver.Index;
import com.example.demo.service.support.PhongStringResolver.Kind;
import com.example.demo.service.support.PhongStringResolver.Match;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LopHocPhongDualWriteServiceImpl implements LopHocPhongDualWriteService {

    @Value("${eduport.tkb.dual-write-phong.enabled:true}")
    private boolean dualWriteEnabled;

    private final PhongHocRepository phongHocRepository;
    private final PhongStringResolver phongStringResolver;
    private final EntityManager entityManager;

    @Override
    public void synchronize(LopHocPhan lhp) {
        if (!dualWriteEnabled) {
            return;
        }

        fillFkFromJsonIfMissing(lhp);
        writeJsonFromFkIfPossible(lhp);
    }

    private void writeJsonFromFkIfPossible(LopHocPhan lhp) {
        PhongHoc fk = lhp.getPhongHoc();
        if (fk == null) {
            return;
        }

        List<Map<String, Object>> jsonIn = lhp.getThoiKhoaBieuJson();
        if (jsonIn == null || jsonIn.isEmpty()) {
            log.trace("dual-write: LHP {} có FK phòng nhưng JSON slot trống — bỏ qua ghép chuỗi phòng", lhp.getIdLopHp());
            return;
        }

        List<Map<String, Object>> rebuilt = cloneJson(jsonIn);
        String canon = fk.getMaPhong();
        if (canon != null && !canon.isEmpty()) {
            Map<String, Object> first = rebuilt.getFirst();
            first.put("phong", canon);
        }
        lhp.setThoiKhoaBieuJson(rebuilt);
    }

    private void fillFkFromJsonIfMissing(LopHocPhan lhp) {
        if (lhp.getPhongHoc() != null) {
            return;
        }
        List<Map<String, Object>> json = lhp.getThoiKhoaBieuJson();
        if (json == null || json.isEmpty()) {
            return;
        }

        Index idx = phongStringResolver.buildIndex(phongHocRepository.findAll());
        for (Map<String, Object> slot : json) {
            Match m = phongStringResolver.resolve(idx, slot.get("phong"));
            if (m.kind() != Kind.UNIQUE) {
                continue;
            }
            Long id = m.uniqueId().orElse(null);
            if (id == null) {
                continue;
            }
            PhongHoc ref = entityManager.getReference(PhongHoc.class, id);
            lhp.setPhongHoc(ref);
            log.debug("dual-write: LHP {} gán FK từ JSON phòng → {}", lhp.getIdLopHp(), id);
            return;
        }
    }

    /** Copy nông để JPA/ghi nhận dirty JSON và tránh chỉnh chung mutable map bên ngoài. */
    private static List<Map<String, Object>> cloneJson(List<Map<String, Object>> src) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> slot : src) {
            Map<String, Object> m = slot == null ? new LinkedHashMap<>() : new LinkedHashMap<>(slot);
            result.add(m);
        }
        return result;
    }
}
