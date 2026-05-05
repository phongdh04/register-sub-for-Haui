package com.example.demo.service.impl;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.metrics.TkbPhongMappingMetrics;
import com.example.demo.payload.response.PhongJsonApplyResponse;
import com.example.demo.payload.response.PhongJsonAuditResponse;
import com.example.demo.payload.response.PhongJsonAuditSummary;
import com.example.demo.payload.response.PhongJsonLhpAuditItem;
import com.example.demo.payload.response.PhongJsonSlotAuditItem;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.service.IPhongJsonMigrationService;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.service.LopHocPhongDualWriteService;
import com.example.demo.service.support.PhongStringResolver;
import com.example.demo.service.support.PhongStringResolver.Index;
import com.example.demo.service.support.PhongStringResolver.Kind;
import com.example.demo.service.support.PhongStringResolver.Match;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhongJsonMigrationServiceImpl implements IPhongJsonMigrationService {

    public static final String SCHEMA_AUDIT_V1 = "eduport-phong-json-audit.v1";

    private static final int MAX_DETAIL_ROWS = 800;

    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final PhongHocRepository phongHocRepository;
    private final PhongStringResolver phongStringResolver;
    private final EntityManager entityManager;
    private final TkbPhongMappingMetrics mappingMetrics;
    private final LopHocPhongDualWriteService lopHocPhongDualWriteService;
    private final ITkbRevisionService tkbRevisionService;

    @Override
    @Transactional(readOnly = true)
    public PhongJsonAuditResponse audit(Long hocKyId, boolean includeDetails) {
        hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));

        List<LopHocPhan> list = lopHocPhanRepository.findByHocKy_IdHocKy(hocKyId);
        Index idx = phongStringResolver.buildIndex(phongHocRepository.findAll());

        PhongJsonAuditSummary summary = PhongJsonAuditSummary.builder()
                .totalLhpInHocKy(list.size())
                .build();

        List<PhongJsonLhpAuditItem> allDetails = new ArrayList<>();
        for (LopHocPhan lhp : list) {
            PhongJsonLhpAuditItem row = buildLhpAudit(lhp, idx);
            tally(summary, row);
            allDetails.add(row);
        }

        boolean truncatedFlag = includeDetails && allDetails.size() > MAX_DETAIL_ROWS;
        List<PhongJsonLhpAuditItem> detailsOut = includeDetails
                ? (truncatedFlag ? new ArrayList<>(allDetails.subList(0, MAX_DETAIL_ROWS)) : allDetails)
                : List.of();

        log.info(
                "TKB audit phòng_JSON hocKyId={} totalLhp={}{}",
                hocKyId,
                list.size(),
                truncatedFlag ? " details_truncated=" + MAX_DETAIL_ROWS : "");

        return PhongJsonAuditResponse.builder()
                .hocKyId(hocKyId)
                .schemaVersion(SCHEMA_AUDIT_V1)
                .summary(summary)
                .details(detailsOut)
                .detailsTruncated(truncatedFlag)
                .detailsTotalBeforeTruncation(includeDetails ? allDetails.size() : null)
                .build();
    }

    @Override
    @Transactional
    public PhongJsonApplyResponse apply(Long hocKyId, boolean dryRun) {
        hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));

        Index idx = phongStringResolver.buildIndex(phongHocRepository.findAll());
        List<LopHocPhan> list = lopHocPhanRepository.findByHocKy_IdHocKy(hocKyId);

        int examined = 0;
        int updated = 0;
        int skipFk = 0;
        int skipNoJson = 0;
        int skipUnresolvable = 0;
        List<Long> unresolvedIds = new ArrayList<>();
        List<Long> ambiguousIds = new ArrayList<>();

        for (LopHocPhan lhp : list) {
            examined++;
            if (lhp.getPhongHoc() != null) {
                skipFk++;
                continue;
            }

            List<Map<String, Object>> json = lhp.getThoiKhoaBieuJson();
            if (json == null || json.isEmpty()) {
                skipNoJson++;
                mappingMetrics.incrementFallbackJsonPhong();
                continue;
            }

            PhongJsonLhpAuditItem auditRow = buildLhpAudit(lhp, idx);
            Long fk = auditRow.getSuggestedFkPhongId();
            boolean slotAmbiguous = auditRow.getSlots() != null
                    && auditRow.getSlots().stream().anyMatch(s -> "AMBIGUOUS".equals(s.getResolution()));

            if (fk == null) {
                skipUnresolvable++;
                unresolvedIds.add(lhp.getIdLopHp());
                mappingMetrics.incrementFallbackJsonPhong();
                if (slotAmbiguous || "HAS_AMBIGUOUS".equals(auditRow.getOverallCategory())) {
                    ambiguousIds.add(lhp.getIdLopHp());
                }
                continue;
            }

            updated++;
            if (!dryRun) {
                PhongHoc refPhong = entityManager.getReference(PhongHoc.class, fk);
                lhp.setPhongHoc(refPhong);
                lopHocPhongDualWriteService.synchronize(lhp);
                lopHocPhanRepository.save(lhp);
                log.debug("Mapped id_phong_hoc={} for LHP id={} ma={}", fk, lhp.getIdLopHp(), lhp.getMaLopHp());
            }
        }

        log.info(
                "TKB apply_JSON_to_FK hocKyId={} dryRun={} examined={} updated={} skipHadFk={} skipNoJson={} skipUnresolvable={}",
                hocKyId, dryRun, examined, updated, skipFk, skipNoJson, skipUnresolvable);

        if (!dryRun && updated > 0) {
            tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        }

        return PhongJsonApplyResponse.builder()
                .hocKyId(hocKyId)
                .dryRun(dryRun)
                .examinedLhp(examined)
                .updatedFkCount(updated)
                .skippedAlreadyHadFk(skipFk)
                .skippedNoJson(skipNoJson)
                .skippedNoResolvablePhong(skipUnresolvable)
                .unresolvedLopHpIds(unresolvedIds)
                .ambiguousLopHpIds(ambiguousIds.stream().distinct().toList())
                .build();
    }

    private void tally(PhongJsonAuditSummary s, PhongJsonLhpAuditItem row) {
        switch (row.getOverallCategory()) {
            case "NO_JSON" -> s.setCategoryNoJson(s.getCategoryNoJson() + 1);
            case "EMPTY_JSON" -> {
                s.setCategoryEmptyJson(s.getCategoryEmptyJson() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
            }
            case "NO_PHONG_KEYS" -> {
                s.setCategoryNoPhongKeys(s.getCategoryNoPhongKeys() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
            }
            case "ALL_SLOTS_UNIQUE" -> {
                s.setCategoryAllSlotsUnique(s.getCategoryAllSlotsUnique() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
                bumpPhongKeyIfAny(row, s);
            }
            case "HAS_AMBIGUOUS" -> {
                s.setCategoryHasAmbiguous(s.getCategoryHasAmbiguous() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
                bumpPhongKeyIfAny(row, s);
            }
            case "HAS_NO_MATCH" -> {
                s.setCategoryHasNoMatch(s.getCategoryHasNoMatch() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
                bumpPhongKeyIfAny(row, s);
            }
            case "MIXED" -> {
                s.setCategoryMixed(s.getCategoryMixed() + 1);
                s.setLhpWithNonNullJson(s.getLhpWithNonNullJson() + 1);
                bumpPhongKeyIfAny(row, s);
            }
            default -> { /* ignore unknown */ }
        }
    }

    private static void bumpPhongKeyIfAny(PhongJsonLhpAuditItem row, PhongJsonAuditSummary s) {
        if (row.getSlots() != null && row.getSlots().stream().anyMatch(si ->
                si.getPhongRaw() != null && !si.getPhongRaw().trim().isEmpty())) {
            s.setLhpWithAnyPhongKey(s.getLhpWithAnyPhongKey() + 1);
        }
    }

    private PhongJsonLhpAuditItem buildLhpAudit(LopHocPhan lhp, Index idx) {
        List<Map<String, Object>> json = lhp.getThoiKhoaBieuJson();

        Long currentFk = safePhongFkId(lhp);

        if (json == null) {
            return baseItem(lhp, currentFk, "NO_JSON", null, List.of());
        }
        if (json.isEmpty()) {
            return baseItem(lhp, currentFk, "EMPTY_JSON", null, List.of());
        }

        List<PhongJsonSlotAuditItem> slotsOut = new ArrayList<>();
        boolean seenNonEmptyPhong = false;

        int index = 0;
        for (Map<String, Object> slot : json) {
            Object phRaw = slot.get("phong");
            String rawStr = phRaw == null ? null : String.valueOf(phRaw);
            boolean hasPayload = rawStr != null && !rawStr.trim().isEmpty();
            if (hasPayload) {
                seenNonEmptyPhong = true;
            }

            Match m = phongStringResolver.resolve(idx, phRaw);
            String resolution = resolutionLabel(m.kind());
            Long mid = (m.kind() == Kind.UNIQUE && !m.candidates().isEmpty())
                    ? m.candidates().getFirst().getIdPhong()
                    : null;
            String mma = (m.kind() == Kind.UNIQUE && !m.candidates().isEmpty())
                    ? m.candidates().getFirst().getMaPhong()
                    : null;

            String ambJoin = "";
            if (m.kind() == Kind.AMBIGUOUS) {
                ambJoin = m.candidates().stream()
                        .map(PhongHoc::getMaPhong)
                        .distinct()
                        .sorted()
                        .collect(Collectors.joining(","));
            }

            slotsOut.add(PhongJsonSlotAuditItem.builder()
                    .slotIndex(index++)
                    .phongRaw(hasPayload ? rawStr.trim() : null)
                    .resolution(resolution)
                    .matchedIdPhong(mid)
                    .matchedMaPhong(mma)
                    .ambiguousMaPhongsJoined(
                            (m.kind() == Kind.AMBIGUOUS) && !ambJoin.isEmpty() ? ambJoin : null)
                    .build());
        }

        Long suggestedFk = slotsOut.stream()
                .filter(s -> "UNIQUE".equals(s.getResolution()) && s.getMatchedIdPhong() != null)
                .findFirst()
                .map(PhongJsonSlotAuditItem::getMatchedIdPhong)
                .orElse(null);

        String category;
        if (!seenNonEmptyPhong) {
            category = "NO_PHONG_KEYS";
        } else {
            boolean anyAmbiguous = slotsOut.stream().anyMatch(s -> "AMBIGUOUS".equals(s.getResolution()));
            boolean anyNoMatch = slotsOut.stream().anyMatch(s -> "NO_MATCH".equals(s.getResolution()));
            if (anyAmbiguous && anyNoMatch) {
                category = "MIXED";
            } else if (anyAmbiguous) {
                category = "HAS_AMBIGUOUS";
            } else if (anyNoMatch) {
                category = "HAS_NO_MATCH";
            } else {
                category = "ALL_SLOTS_UNIQUE";
            }
        }

        return baseItem(lhp, currentFk, category, suggestedFk, slotsOut);
    }

    private static String resolutionLabel(Kind kind) {
        return switch (kind) {
            case UNIQUE -> "UNIQUE";
            case AMBIGUOUS -> "AMBIGUOUS";
            case MISSING -> "MISSING";
            case NO_MATCH -> "NO_MATCH";
        };
    }

    private PhongJsonLhpAuditItem baseItem(
            LopHocPhan lhp,
            Long currentFk,
            String category,
            Long suggestedFk,
            List<PhongJsonSlotAuditItem> slots) {

        return PhongJsonLhpAuditItem.builder()
                .idLopHp(lhp.getIdLopHp())
                .maLopHp(lhp.getMaLopHp())
                .idPhongFkCurrent(currentFk)
                .overallCategory(category)
                .suggestedFkPhongId(suggestedFk)
                .slots(slots)
                .build();
    }

    /** Tránh Hibernate proxy lỗi khi chỉ đọc id. */
    private static Long safePhongFkId(LopHocPhan lhp) {
        PhongHoc p = lhp.getPhongHoc();
        return p != null ? p.getIdPhong() : null;
    }
}
