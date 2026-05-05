package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.entity.TkbBlock;
import com.example.demo.domain.enums.TkbTrangThai;
import com.example.demo.payload.request.SchedulingConflictCheckRequest;
import com.example.demo.payload.request.SchedulingSlotPatchRequest;
import com.example.demo.payload.response.SchedulingConflictCheckResponse;
import com.example.demo.payload.response.SchedulingConflictItemResponse;
import com.example.demo.payload.response.SchedulingSlotPatchResponse;
import com.example.demo.payload.response.SchedulingSnapshotResponse;
import com.example.demo.payload.response.SchedulingSnapshotRowResponse;
import com.example.demo.metrics.TkbWorkflowMetrics;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.scheduling.snapshot.SchedulingIndexData;
import com.example.demo.scheduling.snapshot.SchedulingOccupancyOccurrence;
import com.example.demo.scheduling.snapshot.SchedulingSnapshotCache;
import com.example.demo.service.ISchedulingGridAdminService;
import com.example.demo.service.ICohortConflictRuleService;
import com.example.demo.service.SchedulingSnapshotIndexBuilder;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.service.support.SchedulingJsonSlotNormalizer;
import com.example.demo.service.support.SchedulingJsonSlotNormalizer.ParsedTiet;
import com.example.demo.service.support.SchedulingRoomCapacityEvaluator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingGridAdminServiceImpl implements ISchedulingGridAdminService {

    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final PhongHocRepository phongHocRepository;
    private final SchedulingSnapshotCache schedulingSnapshotCache;
    private final ICohortConflictRuleService cohortConflictRuleService;
    private final ITkbRevisionService tkbRevisionService;
    private final TkbWorkflowMetrics tkbWorkflowMetrics;

    @Override
    @Transactional(readOnly = true)
    public SchedulingSnapshotResponse getSnapshot(Long hocKyId) {
        Objects.requireNonNull(hocKyId, "hocKyId");
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        long revision = revisionOrZero(hk.getTkbRevision());

        List<LopHocPhan> lhps = lopHocPhanRepository.findForSchedulingSnapshot(hocKyId);

        var cacheHit = schedulingSnapshotCache.getIfMatchingRevision(hocKyId, revision);
        final String phase;
        if (cacheHit != null) {
            phase = "WARM";
            log.atInfo().log("TKB scheduling snapshot hocKyId={} revision={} cachePhase=WARM (index rows={})",
                    hocKyId, revision, lhps.size());
        } else {
            phase = "COLD";
            SchedulingIndexData rebuilt = SchedulingSnapshotIndexBuilder.buildFromLhps(lhps);
            schedulingSnapshotCache.put(hocKyId, revision, rebuilt);
            log.atInfo().log("TKB scheduling snapshot hocKyId={} revision={} cachePhase=COLD rebuilt index lhps={}",
                    hocKyId, revision, lhps.size());
        }

        List<SchedulingSnapshotRowResponse> rows = new ArrayList<>();
        for (LopHocPhan lhp : lhps) {
            rows.add(toRow(lhp));
        }

        return SchedulingSnapshotResponse.builder()
                .hocKyId(hocKyId)
                .revisionVersion(revision)
                .cachePhase(phase)
                .rows(rows)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SchedulingConflictCheckResponse conflictCheck(Long hocKyId, SchedulingConflictCheckRequest request) {
        String corr = java.util.UUID.randomUUID().toString();
        var sample = tkbWorkflowMetrics.start();
        Objects.requireNonNull(hocKyId, "hocKyId");
        Objects.requireNonNull(request, "request");

        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        long revision = revisionOrZero(hk.getTkbRevision());

        LopHocPhan self = lopHocPhanRepository.findById(request.getIdLopHp())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp HP: " + request.getIdLopHp()));

        Long selfHkId = Optional.ofNullable(self.getHocKy()).map(HocKy::getIdHocKy).orElse(null);
        if (!Objects.equals(selfHkId, hocKyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lớp HP không thuộc học kỳ path: idLopHp=" + request.getIdLopHp());
        }

        List<LopHocPhan> lhpsForIndex = lopHocPhanRepository.findForSchedulingSnapshot(hocKyId);
        var cacheHit = schedulingSnapshotCache.getIfMatchingRevision(hocKyId, revision);
        final SchedulingIndexData index;
        if (cacheHit != null) {
            index = cacheHit.indexData();
        } else {
            SchedulingIndexData rebuilt = SchedulingSnapshotIndexBuilder.buildFromLhps(lhpsForIndex);
            schedulingSnapshotCache.put(hocKyId, revision, rebuilt);
            index = rebuilt;
        }

        List<SchedulingConflictItemResponse> violations = collectDraftConflicts(
                index, request, self, lhpsForIndex, hocKyId, revision);

        SchedulingConflictCheckResponse resp = SchedulingConflictCheckResponse.builder()
                .hocKyId(hocKyId)
                .idLopHpDraft(request.getIdLopHp())
                .revisionVersion(revision)
                .hasConflict(!violations.isEmpty())
                .conflicts(violations)
                .build();
        tkbWorkflowMetrics.markConflictCheck(resp.isHasConflict() ? "conflict" : "ok");
        tkbWorkflowMetrics.stop(sample, "eduport.tkb.conflict_check.latency");
        log.atInfo().log(
                "Conflict-check completed corrId={} hocKyId={} idLopHp={} hasConflict={} count={}",
                corr,
                hocKyId,
                request.getIdLopHp(),
                resp.isHasConflict(),
                resp.getConflicts() != null ? resp.getConflicts().size() : 0);
        return resp;
    }

    @Override
    @Transactional
    public SchedulingSlotPatchResponse patchSlot(Long idLopHp, SchedulingSlotPatchRequest request) {
        Objects.requireNonNull(idLopHp, "idLopHp");
        Objects.requireNonNull(request, "request");
        LopHocPhan lhp = lopHocPhanRepository.findById(idLopHp)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp HP: " + idLopHp));
        Long hocKyId = Optional.ofNullable(lhp.getHocKy()).map(HocKy::getIdHocKy).orElse(null);
        if (hocKyId == null) {
            throw new IllegalArgumentException("LHP không có học kỳ");
        }
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        if (hk.getTkbTrangThai() == TkbTrangThai.CONG_BO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Học kỳ đã CONG_BO, phải chỉnh qua change-set workflow.");
        }
        SchedulingConflictCheckRequest chk = SchedulingConflictCheckRequest.builder()
                .idLopHp(idLopHp)
                .slots(request.getSlots())
                .overrideGiangVienId(request.getOverrideGiangVienId())
                .overridePhongHocId(request.getOverridePhongHocId())
                .build();
        SchedulingConflictCheckResponse checked = conflictCheck(hocKyId, chk);
        if (checked.isHasConflict()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "PATCH slot bị từ chối do hard violation; chạy conflict-check để xem chi tiết.");
        }
        lhp.setThoiKhoaBieuJson(new ArrayList<>(request.getSlots()));
        lopHocPhanRepository.save(lhp);
        tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        long revision = revisionOrZero(hocKyRepository.findById(hocKyId).map(HocKy::getTkbRevision).orElse(0L));
        return SchedulingSlotPatchResponse.builder()
                .hocKyId(hocKyId)
                .idLopHp(idLopHp)
                .persisted(true)
                .revisionVersion(revision)
                .message("PATCH slot thành công")
                .build();
    }

    private List<SchedulingConflictItemResponse> collectDraftConflicts(
            SchedulingIndexData index,
            SchedulingConflictCheckRequest request,
            LopHocPhan selfProbe,
            List<LopHocPhan> lhpsInSameHocKy,
            Long hocKyId,
            long revision) {

        long excludeId = request.getIdLopHp();
        List<SchedulingConflictItemResponse> out = new ArrayList<>();
        List<Map<String, Object>> slots = Optional.ofNullable(request.getSlots()).orElse(List.of());
        Map<Long, LopHocPhan> lhpById = new HashMap<>();
        if (lhpsInSameHocKy != null) {
            for (LopHocPhan lx : lhpsInSameHocKy) {
                lhpById.put(lx.getIdLopHp(), lx);
            }
        }
        int draftSiSo = selfProbe.getSiSoToiDa() != null ? selfProbe.getSiSoToiDa() : 0;

        log.atDebug().log("Conflict-check hocKyId={} revision={} idLopHp={} slots={}",
                hocKyId, revision, excludeId, slots.size());

        for (Map<String, Object> draftSlot : slots) {
            Integer thu = SchedulingJsonSlotNormalizer.coercibleThuInt(draftSlot);
            ParsedTiet tiet = SchedulingJsonSlotNormalizer.parseTiet(draftSlot);
            if (thu == null || !tiet.present()) {
                continue;
            }

            Long roomFk = SchedulingJsonSlotNormalizer.resolvePhongIdFk(draftSlot);
            if (roomFk == null) {
                roomFk = request.getOverridePhongHocId();
            }
            if (roomFk == null) {
                PhongHoc ph = selfProbe.getPhongHoc();
                roomFk = ph != null ? ph.getIdPhong() : null;
            }

            Long gvFk = SchedulingJsonSlotNormalizer.resolveGiangVienIdFk(draftSlot);
            if (gvFk == null) {
                gvFk = request.getOverrideGiangVienId();
            }
            if (gvFk == null) {
                GiangVien gv = selfProbe.getGiangVien();
                gvFk = gv != null ? gv.getIdGiangVien() : null;
            }

            String legacy = SchedulingJsonSlotNormalizer.normalizedLegacyPhong(draftSlot);

            if (roomFk != null) {
                String keyRoom = SchedulingIndexData.roomKey(roomFk, null, thu);
                roomConflictsAgainst(keyRoom, index.roomOccupancyByKey(), excludeId, tiet.bd(), tiet.kt(), out);
                phongHocRepository.findById(roomFk)
                        .ifPresent(ph -> out.addAll(SchedulingRoomCapacityEvaluator.evaluatePhongOverload(
                                ph.getIdPhong(),
                                ph.getSucChua(),
                                keyRoom,
                                tiet.bd(), tiet.kt(),
                                excludeId,
                                draftSiSo,
                                index.roomOccupancyByKey(),
                                lhpById)));
            } else if (!legacy.isEmpty()) {
                String keyLegacy = SchedulingIndexData.roomKey(null, legacy, thu);
                roomConflictsAgainst(keyLegacy, index.roomOccupancyByKey(), excludeId, tiet.bd(), tiet.kt(), out);
                phongHocRepository.findByMaPhongIgnoreCase(legacy)
                        .ifPresent(ph -> out.addAll(SchedulingRoomCapacityEvaluator.evaluatePhongOverload(
                                ph.getIdPhong(),
                                ph.getSucChua(),
                                keyLegacy,
                                tiet.bd(), tiet.kt(),
                                excludeId,
                                draftSiSo,
                                index.roomOccupancyByKey(),
                                lhpById)));
            }

            if (gvFk != null) {
                String gvKey = SchedulingIndexData.gvKey(gvFk, thu);
                gvConflictsAgainst(gvKey, index.gvOccupancyByKey(), excludeId, tiet.bd(), tiet.kt(), out);
            }
        }

        enforceCohortRules(hocKyId, excludeId, slots, lhpById, out);

        return dedupe(out);
    }

    private static void roomConflictsAgainst(
            String key,
            Map<String, List<SchedulingOccupancyOccurrence>> ix,
            long excludeId,
            int bd,
            int kt,
            List<SchedulingConflictItemResponse> sink) {

        List<SchedulingOccupancyOccurrence> list = ix.get(key);
        if (list == null) {
            return;
        }
        for (SchedulingOccupancyOccurrence occ : list) {
            if (occ.idLopHp() == excludeId) {
                continue;
            }
            if (occ.overlaps(bd, kt)) {
                sink.add(SchedulingConflictItemResponse.builder()
                        .conflictType("PHONG_TRUNG")
                        .conflictingLopHpId(occ.idLopHp())
                        .detail(key + " chồng tiết tiết=[" + bd + "-" + kt + "] vs lhp=" + occ.idLopHp())
                        .build());
            }
        }
    }

    private static void gvConflictsAgainst(
            String key,
            Map<String, List<SchedulingOccupancyOccurrence>> ix,
            long excludeId,
            int bd,
            int kt,
            List<SchedulingConflictItemResponse> sink) {

        List<SchedulingOccupancyOccurrence> list = ix.get(key);
        if (list == null) {
            return;
        }
        for (SchedulingOccupancyOccurrence occ : list) {
            if (occ.idLopHp() == excludeId) {
                continue;
            }
            if (occ.overlaps(bd, kt)) {
                sink.add(SchedulingConflictItemResponse.builder()
                        .conflictType("GV_TRUNG")
                        .conflictingLopHpId(occ.idLopHp())
                        .detail(key + " GV chồng tiết=[" + bd + "-" + kt + "] vs lhp=" + occ.idLopHp())
                        .build());
            }
        }
    }

    private static List<SchedulingConflictItemResponse> dedupe(List<SchedulingConflictItemResponse> in) {
        List<SchedulingConflictItemResponse> r = new ArrayList<>();
        for (SchedulingConflictItemResponse it : in) {
            boolean seen = false;
            for (SchedulingConflictItemResponse e : r) {
                if (Objects.equals(e.getConflictType(), it.getConflictType())
                        && Objects.equals(e.getConflictingLopHpId(), it.getConflictingLopHpId())
                        && Objects.equals(e.getDetail(), it.getDetail())) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                r.add(it);
            }
        }
        return r;
    }

    private static SchedulingSnapshotRowResponse toRow(LopHocPhan lhp) {
        HocPhan hp = lhp.getHocPhan();
        GiangVien gv = lhp.getGiangVien();
        PhongHoc ph = lhp.getPhongHoc();
        TkbBlock tb = lhp.getTkbBlock();
        List<Map<String, Object>> jsonCopy =
                lhp.getThoiKhoaBieuJson() != null ? new ArrayList<>(lhp.getThoiKhoaBieuJson()) : List.of();
        String tenGv = gv != null && gv.getTenGiangVien() != null ? gv.getTenGiangVien().trim() : null;
        return SchedulingSnapshotRowResponse.builder()
                .idLopHp(lhp.getIdLopHp())
                .maLopHp(lhp.getMaLopHp())
                .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .idGiangVien(gv != null ? gv.getIdGiangVien() : null)
                .tenGiangVien(tenGv)
                .idPhongHoc(ph != null ? ph.getIdPhong() : null)
                .maPhongHoc(ph != null ? ph.getMaPhong() : null)
                .idTkbBlock(tb != null ? tb.getIdTkbBlock() : null)
                .maTkbBlock(tb != null ? tb.getMaBlock() : null)
                .thoiKhoaBieuJson(jsonCopy)
                .trangThai(lhp.getTrangThai())
                .build();
    }

    private static long revisionOrZero(Long revision) {
        return revision != null ? revision : 0L;
    }

    private void enforceCohortRules(
            Long hocKyId,
            long draftLhpId,
            List<Map<String, Object>> draftSlots,
            Map<Long, LopHocPhan> lhpById,
            List<SchedulingConflictItemResponse> sink) {
        Set<SlotKey> draftAtoms = expandAtomicSlots(draftSlots);
        if (draftAtoms.isEmpty()) {
            return;
        }
        var pairs = cohortConflictRuleService.loadPairRulesByHocKy(hocKyId);
        for (var p : pairs) {
            Long peerId = null;
            if (Objects.equals(p.leftLopHpId(), draftLhpId)) {
                peerId = p.rightLopHpId();
            } else if (Objects.equals(p.rightLopHpId(), draftLhpId)) {
                peerId = p.leftLopHpId();
            }
            if (peerId == null) {
                continue;
            }
            LopHocPhan peer = lhpById.get(peerId);
            if (peer == null) {
                continue;
            }
            Set<SlotKey> peerAtoms = expandAtomicSlots(peer.getThoiKhoaBieuJson());
            boolean hasIntersection = false;
            for (SlotKey d : draftAtoms) {
                if (peerAtoms.contains(d)) {
                    hasIntersection = true;
                    break;
                }
            }
            if (!hasIntersection) {
                sink.add(SchedulingConflictItemResponse.builder()
                        .conflictType("COHORT_TRUNG")
                        .conflictingLopHpId(peerId)
                        .detail("Cohort '" + p.nhomMa() + "' yêu cầu cùng tiết với lhp=" + peerId)
                        .build());
            }
        }
    }

    private static Set<SlotKey> expandAtomicSlots(List<Map<String, Object>> slots) {
        java.util.LinkedHashSet<SlotKey> out = new java.util.LinkedHashSet<>();
        if (slots == null) {
            return out;
        }
        for (Map<String, Object> slot : slots) {
            Integer thu = SchedulingJsonSlotNormalizer.coercibleThuInt(slot);
            ParsedTiet tiet = SchedulingJsonSlotNormalizer.parseTiet(slot);
            if (thu == null || !tiet.present()) {
                continue;
            }
            for (int t = tiet.bd(); t <= tiet.kt(); t++) {
                out.add(new SlotKey(thu, t));
            }
        }
        return out;
    }

    private record SlotKey(int thu, int tiet) {
    }
}
