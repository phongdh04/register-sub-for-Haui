package com.example.demo.service.support;

/**
 * Mã {@code lop_hoc_phan} shell sinh từ kế hoạch PRE (F17) — giới hạn độ dài DB, suffix gắn digest để idempotent.
 */
public final class PreRegistrationPlanShellMaBuilder {

    public static final int MA_LOP_HP_MAX_LEN = 30;

    private PreRegistrationPlanShellMaBuilder() {
    }

    /**
     * Ví dụ: {@code IT6005_R1a2b3c4d_1} — {@code digest8} nên là 8 ký tự hex (chữ thường).
     */
    public static String build(String maHocPhan, String digest8, int sectionOneBased) {
        String d = digest8 == null ? "00000000" : digest8;
        if (d.length() > 8) {
            d = d.substring(0, 8);
        }
        String core = maHocPhan == null ? "HP" : maHocPhan.replaceAll("[^A-Za-z0-9]", "");
        if (core.isEmpty()) {
            core = "HP";
        }
        String suffix = "_R" + d + "_" + sectionOneBased;
        int remain = MA_LOP_HP_MAX_LEN - suffix.length();
        if (remain < 3) {
            return truncate("R" + d + "S" + sectionOneBased, MA_LOP_HP_MAX_LEN);
        }
        if (core.length() > remain) {
            core = core.substring(0, remain);
        }
        String full = core + suffix;
        return full.length() <= MA_LOP_HP_MAX_LEN ? full : truncate(full, MA_LOP_HP_MAX_LEN);
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }
}
