package com.example.demo.service.support;

import com.example.demo.util.TkbSlotConflictUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Đọc slot TKB trong JSON đã seed / dual-write — hỗ trợ lowercase canonical + một số key legacy. */
public final class SchedulingJsonSlotNormalizer {

    private SchedulingJsonSlotNormalizer() {
    }

    public static List<Map<String, Object>> slotsMapsFromStoredJson(List<Map<String, Object>> json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        return json;
    }

    public static Integer coercibleThuInt(Map<String, Object> slot) {
        if (slot == null) {
            return null;
        }
        Object raw = slot.getOrDefault("thu", slot.get("Thu"));
        return asIntBounded(raw, SchedulingThuBounds.MIN, SchedulingThuBounds.MAX);
    }

    public static ParsedTiet parseTiet(Map<String, Object> slot) {
        if (slot == null) {
            return ParsedTiet.missing();
        }
        Object tietRaw = slot.get("tiet");
        String tCompact = asStringTrimOrNull(tietRaw);
        if (tCompact != null && !tCompact.isEmpty()) {
            try {
                int[] r = TkbSlotConflictUtils.parseTiet(tCompact);
                return new ParsedTiet(r[0], r[1], true);
            } catch (Exception ignored) {
                return ParsedTiet.missing();
            }
        }
        Integer bd = parseTiSo(slot.get("TietBD"));
        Integer kt = parseTiSo(slot.get("TietKT"));
        if (bd != null && kt != null && bd <= kt) {
            return new ParsedTiet(bd, kt, true);
        }
        return ParsedTiet.missing();
    }

    /** FK phòng từ slot (nếu có) hoặc null. */
    public static Long resolvePhongIdFk(Map<String, Object> slot) {
        if (slot == null) {
            return null;
        }
        Long fk = extractLongFlexible(slot.get("phong_id_fk"));
        if (fk != null) {
            return fk;
        }
        return extractLongFlexible(slot.get("idPhong"));
    }

    /** FK GV từ slot (nếu có) hoặc null. */
    public static Long resolveGiangVienIdFk(Map<String, Object> slot) {
        if (slot == null) {
            return null;
        }
        Long fk = extractLongFlexible(slot.get("gv_id_fk"));
        if (fk != null) {
            return fk;
        }
        return extractLongFlexible(slot.get("idGiangVien"));
    }

    public static String normalizedLegacyPhong(Map<String, Object> slot) {
        if (slot == null) {
            return "";
        }
        Object raw = slot.getOrDefault("phong", slot.get("Phong"));
        if (raw == null) {
            return "";
        }
        return String.valueOf(raw).trim().toUpperCase(Locale.ROOT);
    }

    static Integer parseTiSo(Object raw) {
        Integer n = parseIntFlexible(raw);
        return n != null && n >= 1 ? n : null;
    }

    public static String asStringTrimOrNull(Object o) {
        if (o == null) {
            return null;
        }
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
    }

    private static Integer asIntBounded(Object raw, int min, int max) {
        Integer v = parseIntFlexible(raw);
        if (v == null || v < min || v > max) {
            return null;
        }
        return v;
    }

    private static Integer parseIntFlexible(Object raw) {
        if (raw instanceof Number n) {
            return n.intValue();
        }
        String s = asStringTrimOrNull(raw);
        if (s == null) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long extractLongFlexible(Object o) {
        if (o instanceof Number n) {
            return n.longValue();
        }
        String s = asStringTrimOrNull(o);
        if (s == null) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record ParsedTiet(int bd, int kt, boolean present) {
        static ParsedTiet missing() {
            return new ParsedTiet(0, 0, false);
        }
    }

    static final class SchedulingThuBounds {
        static final int MIN = 2;
        static final int MAX = 8;

        private SchedulingThuBounds() {
        }
    }
}
