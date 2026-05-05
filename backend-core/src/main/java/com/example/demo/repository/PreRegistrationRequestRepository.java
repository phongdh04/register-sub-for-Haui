package com.example.demo.repository;

import com.example.demo.domain.enums.PreRegistrationRequestStatus;
import com.example.demo.domain.entity.PreRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PreRegistrationRequestRepository extends JpaRepository<PreRegistrationRequest, Long> {
    Optional<PreRegistrationRequest> findByRequestId(UUID requestId);

    Optional<PreRegistrationRequest> findByDedupeKey(String dedupeKey);

    long countByLink_Id(Long linkId);

    long countByLink_IdAndStatus(Long linkId, PreRegistrationRequestStatus status);
}
