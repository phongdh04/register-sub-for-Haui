package com.example.demo.domain.support;

import java.util.List;
import java.util.Map;

/**
 * Cột surrogate {@code thu_tkb} trên {@code Lop_Hoc_Phan} phục vụ index warm-path conflict (§8.2.1).
 * Encoding cố định: đọc key {@code "thu"} của <strong>phần tử đầu</strong> trong {@code thoi_khoa_bieu_json}.
 * Giá trị cho phép: 2–7 (Thứ Hai … Bảy), 8 Chủ nhật — đồng bộ CHECK {@code gv_busy_slot}.
 */
public final class TkbThuSurrogate {

    public static final int THU_MIN = 2;
    public static final int THU_MAX = 8;

    private TkbThuSurrogate() {
    }

    /**
     * @return {@code null} nếu JSON trống, không có slot đầu, không có {@code thu}, hoặc ngoài 2–8.
     */
    public static Short extractThuFromFirstSlot(List<Map<String, Object>> json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        Map<String, Object> first = json.getFirst();
        if (first == null) {
            return null;
        }
        Object raw = first.get("thu");
        if (raw == null) {
            return null;
        }
        int v = coercibleInt(raw);
        if (v < THU_MIN || v > THU_MAX) {
            return null;
        }
        return (short) v;
    }

    private static int coercibleInt(Object raw) {
        if (raw instanceof Number n) {
            return n.intValue();
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
