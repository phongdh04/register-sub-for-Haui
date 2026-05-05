package com.example.demo.repository;

import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.domain.enums.ScheduleChangeSetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleChangeSetRepository extends JpaRepository<ScheduleChangeSet, Long> {

    List<ScheduleChangeSet> findByHocKy_IdHocKyOrderByCreatedAtDesc(Long hocKyId);

    List<ScheduleChangeSet> findByHocKy_IdHocKyAndTrangThaiOrderByCreatedAtDesc(
            Long hocKyId,
            ScheduleChangeSetStatus trangThai);
}
