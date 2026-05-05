package com.example.demo.repository;

import com.example.demo.domain.entity.CohortConflictRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CohortConflictRuleRepository extends JpaRepository<CohortConflictRule, Long> {

    List<CohortConflictRule> findByHocKy_IdHocKyAndIsActiveTrueOrderByNhomMaAscIdAsc(Long hocKyId);
}
