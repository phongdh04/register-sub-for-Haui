package com.example.demo.util;

/**
 * Che một phần email để hiển thị trên UI sau bước đăng nhập.
 */
public final class EmailMaskUtil {

    private EmailMaskUtil() {}

    public static String mask(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return "***";
        }
        String local = email.substring(0, at);
        String domain = email.substring(at);
        String visible = local.length() <= 1 ? local : local.substring(0, 1);
        return visible + "***" + domain;
    }
}
