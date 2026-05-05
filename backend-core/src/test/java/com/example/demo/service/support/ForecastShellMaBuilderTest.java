package com.example.demo.service.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastShellMaBuilderTest {

    @Test
    void staysWithinMaxLen() {
        String m = ForecastShellMaBuilder.build("VeryLongMaHocPhanNameThatShouldBeClipped", 999_999_999L, 12);
        assertTrue(m.length() <= ForecastShellMaBuilder.MA_LOP_HP_MAX_LEN);
    }

    @Test
    void stableShapeForShortCode() {
        String m = assertDoesNotThrow(() -> ForecastShellMaBuilder.build("IT6005", 123L, 2));
        assertTrue(m.contains("_V123_2"));
        assertTrue(m.length() <= ForecastShellMaBuilder.MA_LOP_HP_MAX_LEN);
    }
}
