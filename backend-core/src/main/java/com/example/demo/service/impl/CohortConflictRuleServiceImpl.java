package com.example.demo.service.impl;

import com.example.demo.domain.entity.CohortConflictRule;
import com.example.demo.repository.CohortConflictRuleRepository;
import com.example.demo.service.ICohortConflictRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CohortConflictRuleServiceImpl implements ICohortConflictRuleService {

    private final CohortConflictRuleRepository cohortConflictRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<Long>> loadGroupMembersByHocKy(Long hocKyId) {
        Objects.requireNonNull(hocKyId, "hocKyId");
        List<CohortConflictRule> rows =
                cohortConflictRuleRepository.findByHocKy_IdHocKyAndIsActiveTrueOrderByNhomMaAscIdAsc(hocKyId);
        Map<String, Set<Long>> out = new LinkedHashMap<>();
        for (CohortConflictRule r : rows) {
            String group = normalizeGroup(r.getNhomMa());
            Long lhpId = r.getLopHocPhan() != null ? r.getLopHocPhan().getIdLopHp() : null;
            if (group == null || lhpId == null) {
                continue;
            }
            out.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(lhpId);
        }
        log.atDebug().log("Loaded cohort groups hocKyId={} groups={} rows={}", hocKyId, out.size(), rows.size());
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PairRule> loadPairRulesByHocKy(Long hocKyId) {
        Map<String, Set<Long>> groups = loadGroupMembersByHocKy(hocKyId);
        List<PairRule> out = new ArrayList<>();
        for (Map.Entry<String, Set<Long>> e : groups.entrySet()) {
            List<Long> members = e.getValue().stream().sorted().toList();
            if (members.size() < 2) {
                continue;
            }
            for (int i = 0; i < members.size(); i++) {
                for (int j = i + 1; j < members.size(); j++) {
                    out.add(new PairRule(members.get(i), members.get(j), e.getKey()));
                }
            }
        }
        return List.copyOf(out);
    }

    private static String normalizeGroup(String nhomMa) {
        if (nhomMa == null) {
            return null;
        }
        String s = nhomMa.trim();
        return s.isEmpty() ? null : s;
    }
}
