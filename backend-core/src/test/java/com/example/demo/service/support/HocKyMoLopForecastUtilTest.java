package com.example.demo.service.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HocKyMoLopForecastUtilTest {

    @Test
    void parseNamHoc_acceptsDashAndUnderscore() {
        assertEquals(2024, HocKyMoLopForecastUtil.parseNamHocStartYear("2024-2025"));
        assertEquals(2023, HocKyMoLopForecastUtil.parseNamHocStartYear("2023_2024"));
    }

    @Test
    void kyLogic_matchesDocExample() {
        int namNhap = 2022;
        int namDauHk = 2024;
        int kyThu = 1;
        assertEquals(5, HocKyMoLopForecastUtil.kyLogicSinhVien(namNhap, namDauHk, kyThu));
    }

    @Test
    void parseNamHoc_rejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> HocKyMoLopForecastUtil.parseNamHocStartYear(" "));
    }
}
