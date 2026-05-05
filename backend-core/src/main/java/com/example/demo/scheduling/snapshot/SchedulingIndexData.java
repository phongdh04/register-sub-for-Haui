package com.example.demo.scheduling.snapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Biểu diễn nội bộ cho overlap O(k) trong conflict-check (warm cache).
 */
public record SchedulingIndexData(Map<String, List<SchedulingOccupancyOccurrence>> roomOccupancyByKey,
                                   Map<String, List<SchedulingOccupancyOccurrence>> gvOccupancyByKey) {

    public static String roomKey(Long phongId, String legacyPhongNorm, Integer thu) {
        String tPart = ":T:" + thu;
        if (phongId != null) {
            return "P:" + phongId + tPart;
        }
        String leg = legacyPhongNorm == null ? "" : legacyPhongNorm;
        return "PS:" + leg + tPart;
    }

    public static String gvKey(long gvId, int thu) {
        return "G:" + gvId + ":T:" + thu;
    }

    static String normalizeLegacyPhong(Object raw) {
        if (raw == null) {
            return "";
        }
        return String.valueOf(raw).trim().toUpperCase(Locale.ROOT);
    }

    /** Factory cho map có thể ghi được. */
    public static SchedulingIndexData emptyMutable() {
        return new SchedulingIndexData(new HashMap<>(), new HashMap<>());
    }

    /** @return HOT (cache miss tính rebuild) hay WARM. */
    public static String phaseLabel(boolean warm) {
        return warm ? "WARM" : "COLD";
    }
}
