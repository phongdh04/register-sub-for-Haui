package com.example.demo.util;

import java.util.List;
import java.util.Map;

/**
 * So khớp slot TKB JSON (thu + tiết) — dùng cho giỏ trước giờ G và có thể tái sử dụng nơi khác.
 */
public final class TkbSlotConflictUtils {

    private TkbSlotConflictUtils() {
    }

    public static boolean listsConflict(List<Map<String, Object>> tkbA, List<Map<String, Object>> tkbB) {
        if (tkbA == null || tkbB == null || tkbA.isEmpty() || tkbB.isEmpty()) {
            return false;
        }
        for (Map<String, Object> slotA : tkbA) {
            for (Map<String, Object> slotB : tkbB) {
                if (slotsConflict(slotA, slotB)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean slotsConflict(Map<String, Object> slotA, Map<String, Object> slotB) {
        Object thuA = slotA.get("thu");
        Object thuB = slotB.get("thu");
        if (thuA == null || !thuA.equals(thuB)) {
            return false;
        }
        String tietA = String.valueOf(slotA.getOrDefault("tiet", ""));
        String tietB = String.valueOf(slotB.getOrDefault("tiet", ""));
        return tietOverlap(tietA, tietB);
    }

    public static boolean tietOverlap(String tietA, String tietB) {
        try {
            int[] rangeA = parseTiet(tietA);
            int[] rangeB = parseTiet(tietB);
            return rangeA[0] <= rangeB[1] && rangeB[0] <= rangeA[1];
        } catch (Exception e) {
            return false;
        }
    }

    public static int[] parseTiet(String tiet) {
        if (tiet.contains("-")) {
            String[] parts = tiet.split("-");
            return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
        }
        int t = Integer.parseInt(tiet.trim());
        return new int[]{t, t};
    }
}
