package com.example.demo.repository;

import com.example.demo.domain.entity.RegistrationCampaign;
import com.example.demo.domain.enums.RegistrationPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho {@link RegistrationCampaign}.
 */
@Repository
public interface RegistrationCampaignRepository extends JpaRepository<RegistrationCampaign, Long> {

    /**
     * Tim campaign theo nam nhap hoc + phase.
     */
    Optional<RegistrationCampaign> findByNamNhapHocAndPhase(Integer namNhapHoc, RegistrationPhase phase);

    /**
     * Tim tat ca campaign cua mot nam nhap hoc.
     */
    List<RegistrationCampaign> findByNamNhapHocOrderByPhaseAsc(Integer namNhapHoc);

    /**
     * Tim tat ca campaign dang mo (theo thoi gian hien tai).
     */
    @Query("""
            SELECT c FROM RegistrationCampaign c
            WHERE c.openAt <= CURRENT_TIMESTAMP
              AND c.closeAt >= CURRENT_TIMESTAMP
            ORDER BY c.namNhapHoc, c.phase
            """)
    List<RegistrationCampaign> findAllActive();

    /**
     * Kiem tra ton tai campaign theo (namNhapHoc, phase).
     */
    boolean existsByNamNhapHocAndPhase(Integer namNhapHoc, RegistrationPhase phase);

    /**
     * Danh sach tat ca campaign, sap xep theo nam nhap hoc.
     */
    List<RegistrationCampaign> findAllByOrderByNamNhapHocDescPhaseAsc();
}
