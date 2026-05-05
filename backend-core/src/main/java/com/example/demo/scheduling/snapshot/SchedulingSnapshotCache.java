package com.example.demo.scheduling.snapshot;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BK-TKB-011: cache index theo học kỳ; invalidate khi {@code tkb_revision} bump.
 */
@Component
public class SchedulingSnapshotCache {

    public record CacheEntry(long revisionVersion, SchedulingIndexData indexData, Instant builtAt) {
    }

    private final ConcurrentHashMap<Long, CacheEntry> entries = new ConcurrentHashMap<>();

    public void invalidate(Long hocKyId) {
        if (hocKyId == null) {
            return;
        }
        entries.remove(hocKyId);
    }

    public CacheEntry getIfMatchingRevision(Long hocKyId, long revisionExpected) {
        CacheEntry ce = entries.get(hocKyId);
        if (ce == null) {
            return null;
        }
        if (ce.revisionVersion() != revisionExpected) {
            invalidate(hocKyId);
            return null;
        }
        return ce;
    }

    public void put(Long hocKyId, long revisionVersion, SchedulingIndexData indexData) {
        entries.put(hocKyId, new CacheEntry(revisionVersion, indexData, Instant.now()));
    }
}
