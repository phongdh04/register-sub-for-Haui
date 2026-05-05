package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.domain.entity.TkbChinhSuaLog;
import com.example.demo.domain.enums.ScheduleChangeSetStatus;
import com.example.demo.metrics.TkbWorkflowMetrics;
import com.example.demo.payload.response.ScheduleImpactAnalysisResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.ScheduleChangeSetRepository;
import com.example.demo.repository.TkbChinhSuaLogRepository;
import com.example.demo.service.INotificationQueueService;
import com.example.demo.service.IScheduleChangeSetService;
import com.example.demo.service.ITkbRevisionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleChangeSetServiceImpl implements IScheduleChangeSetService {

    private final HocKyRepository hocKyRepository;
    private final ScheduleChangeSetRepository scheduleChangeSetRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final TkbChinhSuaLogRepository tkbChinhSuaLogRepository;
    private final INotificationQueueService notificationQueueService;
    private final ITkbRevisionService tkbRevisionService;
    private final TkbWorkflowMetrics tkbWorkflowMetrics;

    @Override
    @Transactional
    public ScheduleChangeSet createPending(Long hocKyId, Map<String, Object> payloadDelta, String requestedBy, String note) {
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        if (payloadDelta == null || payloadDelta.isEmpty()) {
            throw new IllegalArgumentException("payloadDelta không được rỗng");
        }
        ScheduleImpactAnalysisResponse impact = analyzeImpact(hocKyId, payloadDelta);
        ScheduleChangeSet cs = ScheduleChangeSet.builder()
                .hocKy(hk)
                .trangThai(ScheduleChangeSetStatus.PENDING)
                .payloadDelta(new LinkedHashMap<>(payloadDelta))
                .requestedBy(requestedBy)
                .ghiChu(note)
                .affectedSvCount(impact.affectedSvCount())
                .affectedSvIds(impact.affectedSvIds())
                .build();
        ScheduleChangeSet saved = scheduleChangeSetRepository.save(cs);
        hk.setPendingChangeSet(saved);
        hocKyRepository.save(hk);
        notificationQueueService.enqueueScheduleChanged(saved, impact.affectedSvIds());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleChangeSet> listByHocKy(Long hocKyId) {
        return scheduleChangeSetRepository.findByHocKy_IdHocKyOrderByCreatedAtDesc(hocKyId);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleImpactAnalysisResponse analyzeImpact(Long hocKyId, Map<String, Object> payloadDelta) {
        Set<Long> targetLhpIds = extractTargetLopHpIds(payloadDelta);
        if (targetLhpIds.isEmpty()) {
            return new ScheduleImpactAnalysisResponse(0, List.of(), List.of());
        }
        Set<Long> svIds = dangKyHocPhanRepository.findDistinctSuccessSinhVienIdsByHocKyAndLopHpIds(hocKyId, targetLhpIds);
        List<Long> sortedSvIds = svIds.stream().sorted().toList();
        List<Long> sortedLhp = targetLhpIds.stream().sorted().toList();
        return new ScheduleImpactAnalysisResponse(sortedSvIds.size(), sortedSvIds, sortedLhp);
    }

    @Override
    @Transactional
    public ScheduleChangeSet review(
            Long hocKyId,
            Long changeSetId,
            boolean approve,
            String reviewedBy,
            String lyDoThayDoi,
            String note) {
        ScheduleChangeSet cs = mustFindChangeSet(hocKyId, changeSetId);
        if (cs.getTrangThai() != ScheduleChangeSetStatus.PENDING) {
            throw new IllegalArgumentException("Change set không ở trạng thái PENDING: id=" + changeSetId);
        }
        if (lyDoThayDoi == null || lyDoThayDoi.isBlank()) {
            throw new IllegalArgumentException("lyDoThayDoi là bắt buộc");
        }
        if (approve) {
            cs.setTrangThai(ScheduleChangeSetStatus.APPROVED);
            cs.setApprovedAt(Instant.now());
        } else {
            cs.setTrangThai(ScheduleChangeSetStatus.REJECTED);
        }
        cs.setReviewedBy(reviewedBy);
        if (note != null && !note.isBlank()) {
            cs.setGhiChu(note);
        }
        ScheduleChangeSet saved = scheduleChangeSetRepository.save(cs);
        if (!approve) {
            HocKy hk = saved.getHocKy();
            if (hk != null && hk.getPendingChangeSet() != null
                    && Objects.equals(hk.getPendingChangeSet().getId(), saved.getId())) {
                hk.setPendingChangeSet(null);
                hocKyRepository.save(hk);
            }
        }
        writeAuditRows(saved, approve ? "REVIEW_APPROVE" : "REVIEW_REJECT", reviewedBy, lyDoThayDoi, null);
        return saved;
    }

    @Override
    @Transactional
    public ScheduleChangeSet apply(Long hocKyId, Long changeSetId, String appliedBy, String lyDoThayDoi) {
        String corr = java.util.UUID.randomUUID().toString();
        var sample = tkbWorkflowMetrics.start();
        ScheduleChangeSet cs = mustFindChangeSet(hocKyId, changeSetId);
        if (cs.getTrangThai() != ScheduleChangeSetStatus.APPROVED) {
            throw new IllegalArgumentException("Chỉ áp dụng change set đã APPROVED");
        }
        if (lyDoThayDoi == null || lyDoThayDoi.isBlank()) {
            throw new IllegalArgumentException("lyDoThayDoi là bắt buộc");
        }

        long effectiveVersionNo = (cs.getHocKy().getTkbRevision() != null ? cs.getHocKy().getTkbRevision() : 0L) + 1L;
        applyPayloadDeltaToLhps(cs.getPayloadDelta());

        cs.setTrangThai(ScheduleChangeSetStatus.APPLIED);
        cs.setAppliedAt(Instant.now());
        cs.setEffectiveVersionNo(effectiveVersionNo);
        ScheduleChangeSet saved = scheduleChangeSetRepository.save(cs);

        HocKy hk = saved.getHocKy();
        if (hk != null && hk.getPendingChangeSet() != null
                && Objects.equals(hk.getPendingChangeSet().getId(), saved.getId())) {
            hk.setPendingChangeSet(null);
            hocKyRepository.save(hk);
        }
        tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        writeAuditRows(saved, "APPLY", appliedBy, lyDoThayDoi, effectiveVersionNo);
        tkbWorkflowMetrics.markChangeSetApply();
        tkbWorkflowMetrics.stop(sample, "eduport.tkb.change_set.apply.latency");
        org.slf4j.LoggerFactory.getLogger(ScheduleChangeSetServiceImpl.class).info(
                "Change-set apply completed corrId={} hocKy={} changeSet={} effectiveVersionNo={}",
                corr, hocKyId, changeSetId, effectiveVersionNo);
        return saved;
    }

    @SuppressWarnings("unchecked")
    private static Set<Long> extractTargetLopHpIds(Map<String, Object> payloadDelta) {
        Set<Long> out = new LinkedHashSet<>();
        if (payloadDelta == null) {
            return out;
        }
        collectLongs(payloadDelta.get("idLopHp"), out);
        collectLongs(payloadDelta.get("id_lop_hp"), out);
        collectLongs(payloadDelta.get("targetLopHpIds"), out);
        Object slots = payloadDelta.get("slots");
        if (slots instanceof List<?> list) {
            for (Object it : list) {
                if (it instanceof Map<?, ?> slotMap) {
                    collectLongs(((Map<String, Object>) slotMap).get("idLopHp"), out);
                    collectLongs(((Map<String, Object>) slotMap).get("id_lop_hp"), out);
                }
            }
        }
        Object changes = payloadDelta.get("changes");
        if (changes instanceof List<?> list) {
            for (Object it : list) {
                if (it instanceof Map<?, ?> c) {
                    collectLongs(((Map<String, Object>) c).get("idLopHp"), out);
                    collectLongs(((Map<String, Object>) c).get("id_lop_hp"), out);
                }
            }
        }
        return out;
    }

    private static void collectLongs(Object raw, Set<Long> out) {
        if (raw == null) {
            return;
        }
        if (raw instanceof Number n) {
            out.add(n.longValue());
            return;
        }
        if (raw instanceof String s) {
            try {
                out.add(Long.parseLong(s.trim()));
            } catch (NumberFormatException ignored) {
            }
            return;
        }
        if (raw instanceof List<?> list) {
            for (Object x : list) {
                collectLongs(x, out);
            }
        }
    }

    private ScheduleChangeSet mustFindChangeSet(Long hocKyId, Long changeSetId) {
        ScheduleChangeSet cs = scheduleChangeSetRepository.findById(changeSetId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy change set: " + changeSetId));
        Long inHk = cs.getHocKy() != null ? cs.getHocKy().getIdHocKy() : null;
        if (!Objects.equals(inHk, hocKyId)) {
            throw new IllegalArgumentException("Change set không thuộc học kỳ path");
        }
        return cs;
    }

    @SuppressWarnings("unchecked")
    private void applyPayloadDeltaToLhps(Map<String, Object> payloadDelta) {
        if (payloadDelta == null) {
            return;
        }
        Object changes = payloadDelta.get("changes");
        if (!(changes instanceof List<?> list)) {
            return;
        }
        for (Object it : list) {
            if (!(it instanceof Map<?, ?> m)) {
                continue;
            }
            Long idLopHp = toLong(((Map<String, Object>) m).get("idLopHp"));
            if (idLopHp == null) {
                idLopHp = toLong(((Map<String, Object>) m).get("id_lop_hp"));
            }
            Object slots = ((Map<String, Object>) m).get("slots");
            if (idLopHp == null || !(slots instanceof List<?>)) {
                continue;
            }
            final Long lhpId = idLopHp;
            LopHocPhan lhp = lopHocPhanRepository.findById(lhpId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy LHP: " + lhpId));
            List<Map<String, Object>> slotMaps = new ArrayList<>();
            for (Object s : (List<?>) slots) {
                if (s instanceof Map<?, ?> mm) {
                    slotMaps.add(new LinkedHashMap<>((Map<String, Object>) mm));
                }
            }
            lhp.setThoiKhoaBieuJson(slotMaps);
            lopHocPhanRepository.save(lhp);
        }
    }

    private void writeAuditRows(
            ScheduleChangeSet cs,
            String action,
            String actor,
            String lyDoThayDoi,
            Long effectiveVersionNo) {
        Set<Long> targetIds = extractTargetLopHpIds(cs.getPayloadDelta());
        if (targetIds.isEmpty()) {
            targetIds = Set.of(-1L);
        }
        for (Long id : targetIds) {
            LopHocPhan lhp = id > 0 ? lopHocPhanRepository.findById(id).orElse(null) : null;
            Map<String, Object> before = new LinkedHashMap<>();
            if (lhp != null && lhp.getThoiKhoaBieuJson() != null) {
                before.put("slots", lhp.getThoiKhoaBieuJson());
            }
            TkbChinhSuaLog row = TkbChinhSuaLog.builder()
                    .hocKy(cs.getHocKy())
                    .changeSet(cs)
                    .lopHocPhan(lhp)
                    .hanhDong(action)
                    .nguoiThucHien(actor)
                    .lyDoThayDoi(lyDoThayDoi)
                    .payloadCu(before)
                    .payloadMoi(cs.getPayloadDelta())
                    .effectiveVersionNo(effectiveVersionNo)
                    .build();
            tkbChinhSuaLogRepository.save(row);
        }
    }

    private static Long toLong(Object raw) {
        if (raw instanceof Number n) {
            return n.longValue();
        }
        if (raw instanceof String s) {
            try {
                return Long.parseLong(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
