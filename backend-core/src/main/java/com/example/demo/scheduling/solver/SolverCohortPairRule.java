package com.example.demo.scheduling.solver;

/** BACK-TKB-027 — cặp LHP phải trùng ô tiết theo cùng cohort group. */
public record SolverCohortPairRule(Long leftLopHpId, Long rightLopHpId, String nhomMa) {
}
