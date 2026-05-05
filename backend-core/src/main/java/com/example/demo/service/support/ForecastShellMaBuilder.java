package com.example.demo.service.support;

/** Mã lớp shell từ dự báo — deterministích, không vượt quá chiều dài DB. */
public final class ForecastShellMaBuilder {

    /** Độ dài tối đa cột {@code ma_lop_hp} trong bảng LHP (entity). */
    public static final int MA_LOP_HP_MAX_LEN = 30;

    private ForecastShellMaBuilder() {
    }

    /** Ví dụ: {@code IT6005_V123_2} hoặc rút ngắn nếu cần. */
    public static String build(String maHocPhan, long forecastVersionId, int sectionOneBased) {
        String core = maHocPhan == null ? "HP" : maHocPhan.replaceAll("[^A-Za-z0-9]", "");
        if (core.isEmpty()) {
            core = "HP";
        }
        String suffix = "_V" + forecastVersionId + "_" + sectionOneBased;
        int remain = MA_LOP_HP_MAX_LEN - suffix.length();
        if (remain < 3) {
            return truncate("V" + forecastVersionId + "S" + sectionOneBased, MA_LOP_HP_MAX_LEN);
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
