package com.example.demo.service.impl;

import com.example.demo.repository.HocKyRepository;
import com.example.demo.scheduling.snapshot.SchedulingSnapshotCache;
import com.example.demo.service.ITkbRevisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TkbRevisionServiceImpl implements ITkbRevisionService {

    private final HocKyRepository hocKyRepository;
    private final SchedulingSnapshotCache schedulingSnapshotCache;

    @Override
    @Transactional
    public void bumpAfterTkbMutation(Long hocKyId) {
        if (hocKyId == null) {
            return;
        }
        int rows = hocKyRepository.bumpTkbRevision(hocKyId);
        schedulingSnapshotCache.invalidate(hocKyId);
        log.debug("TKB revision bump hocKyId={} rowsAffected={} cache=invalidate", hocKyId, rows);
    }
}
