package com.example.demo.domain.enums;

/**
 * Hai pha đăng ký trong vòng đời 1 học kỳ.
 *
 * - {@link #PRE}: đăng ký dự kiến (thu thập nguyện vọng, không cấp slot lớp).
 * - {@link #OFFICIAL}: đăng ký chính thức (cấp slot lớp, sinh thời khóa biểu).
 */
public enum RegistrationPhase {
    PRE,
    OFFICIAL
}
