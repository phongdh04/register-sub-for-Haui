package com.example.demo.repository;

import com.example.demo.domain.entity.PreRegistrationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreRegistrationLinkRepository extends JpaRepository<PreRegistrationLink, Long> {
    Optional<PreRegistrationLink> findByTokenHash(String tokenHash);

    List<PreRegistrationLink> findAllByOrderByCreatedAtDesc();
}
