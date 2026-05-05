package com.example.demo.service.impl;

import com.example.demo.domain.entity.CohortConflictRule;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.repository.CohortConflictRuleRepository;
import com.example.demo.service.ICohortConflictRuleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CohortConflictRuleServiceImplTest {

    @Mock
    private CohortConflictRuleRepository cohortConflictRuleRepository;

    @InjectMocks
    private CohortConflictRuleServiceImpl service;

    @Test
    void loadGroupMembers_groupsAndDedupesByNhomMa() {
        CohortConflictRule r1 = CohortConflictRule.builder()
                .nhomMa("N1")
                .lopHocPhan(LopHocPhan.builder().idLopHp(10L).build())
                .build();
        CohortConflictRule r2 = CohortConflictRule.builder()
                .nhomMa("N1")
                .lopHocPhan(LopHocPhan.builder().idLopHp(11L).build())
                .build();
        CohortConflictRule r3 = CohortConflictRule.builder()
                .nhomMa("N2")
                .lopHocPhan(LopHocPhan.builder().idLopHp(20L).build())
                .build();
        CohortConflictRule duplicated = CohortConflictRule.builder()
                .nhomMa("N1")
                .lopHocPhan(LopHocPhan.builder().idLopHp(10L).build())
                .build();

        when(cohortConflictRuleRepository.findByHocKy_IdHocKyAndIsActiveTrueOrderByNhomMaAscIdAsc(7L))
                .thenReturn(List.of(r1, r2, r3, duplicated));

        Map<String, Set<Long>> out = service.loadGroupMembersByHocKy(7L);

        Assertions.assertEquals(Set.of(10L, 11L), out.get("N1"));
        Assertions.assertEquals(Set.of(20L), out.get("N2"));
    }

    @Test
    void loadPairRules_flattensPairsInsideGroup() {
        CohortConflictRule r1 = CohortConflictRule.builder()
                .nhomMa("N3")
                .lopHocPhan(LopHocPhan.builder().idLopHp(2L).build())
                .build();
        CohortConflictRule r2 = CohortConflictRule.builder()
                .nhomMa("N3")
                .lopHocPhan(LopHocPhan.builder().idLopHp(3L).build())
                .build();
        CohortConflictRule r3 = CohortConflictRule.builder()
                .nhomMa("N3")
                .lopHocPhan(LopHocPhan.builder().idLopHp(5L).build())
                .build();
        when(cohortConflictRuleRepository.findByHocKy_IdHocKyAndIsActiveTrueOrderByNhomMaAscIdAsc(8L))
                .thenReturn(List.of(r1, r2, r3));

        List<ICohortConflictRuleService.PairRule> pairs = service.loadPairRulesByHocKy(8L);

        Assertions.assertEquals(3, pairs.size());
        Assertions.assertTrue(pairs.stream().anyMatch(p -> p.leftLopHpId().equals(2L) && p.rightLopHpId().equals(3L)));
        Assertions.assertTrue(pairs.stream().anyMatch(p -> p.leftLopHpId().equals(2L) && p.rightLopHpId().equals(5L)));
        Assertions.assertTrue(pairs.stream().anyMatch(p -> p.leftLopHpId().equals(3L) && p.rightLopHpId().equals(5L)));
    }
}
