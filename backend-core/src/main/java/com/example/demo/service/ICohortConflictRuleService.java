package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * BACK-TKB-026 — loader nhóm trùng tiết theo học kỳ.
 */
public interface ICohortConflictRuleService {

    /**
     * @return map nhomMa -> tập idLopHp active trong học kỳ.
     */
    Map<String, Set<Long>> loadGroupMembersByHocKy(Long hocKyId);

    /**
     * Flatten thành cặp ràng buộc (a,b) để checker/solver dùng.
     */
    List<PairRule> loadPairRulesByHocKy(Long hocKyId);

    record PairRule(Long leftLopHpId, Long rightLopHpId, String nhomMa) {
    }
}
