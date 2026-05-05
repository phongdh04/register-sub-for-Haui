package com.example.demo.service;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.scheduling.solver.SolverMiniChosenAssignment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BACK-TKB-024 — ghi kết quả solver mini xuống {@code Lop_Hoc_Phan} (JSON + FK) và bump revision.
 */
@Service
@RequiredArgsConstructor
public class SolverAssignmentApplyService {

    private final LopHocPhanRepository lopHocPhanRepository;
    private final PhongHocRepository phongHocRepository;
    private final EntityManager entityManager;
    private final LopHocPhongDualWriteService lopHocPhongDualWriteService;
    private final ITkbRevisionService tkbRevisionService;

    @Transactional
    public int applyAssignments(Long hocKyId, List<SolverMiniChosenAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return 0;
        }
        int n = 0;
        for (SolverMiniChosenAssignment a : assignments) {
            LopHocPhan lhp = lopHocPhanRepository.findById(a.idLopHp())
                    .orElseThrow(() -> new EntityNotFoundException("LHP id=" + a.idLopHp()));
            if (!Objects.equals(lhp.getHocKy().getIdHocKy(), hocKyId)) {
                throw new IllegalArgumentException("LHP " + a.idLopHp() + " không thuộc học kỳ " + hocKyId);
            }
            PhongHoc ph = phongHocRepository.findById(a.roomId())
                    .orElseThrow(() -> new EntityNotFoundException("Phòng id=" + a.roomId()));

            Map<String, Object> slot = new LinkedHashMap<>();
            slot.put("thu", a.thu());
            slot.put("tiet", a.tiet() + "-" + a.tiet());
            slot.put("phong", ph.getMaPhong());
            lhp.setThoiKhoaBieuJson(List.of(slot));
            lhp.setPhongHoc(entityManager.getReference(PhongHoc.class, a.roomId()));
            lhp.setGiangVien(entityManager.getReference(GiangVien.class, a.gvId()));
            lopHocPhongDualWriteService.synchronize(lhp);
            lopHocPhanRepository.save(lhp);
            n++;
        }
        if (n > 0) {
            tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        }
        return n;
    }
}
