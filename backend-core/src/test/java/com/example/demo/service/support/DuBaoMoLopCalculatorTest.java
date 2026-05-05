package com.example.demo.service.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuBaoMoLopCalculatorTest {

    @Test
    void demandAddsCeilRetakeWeighted() {
        assertEquals(
                10,
                DuBaoMoLopCalculator.demandSv(
                        5,
                        7,
                        new BigDecimal("0.60")));
        // ceil(7*0.6)=ceil(4.2)=5 + 5 = 10
    }

    @Test
    void sectionsCeilsAfterBuffer() {
        assertEquals(
                3,
                DuBaoMoLopCalculator.soLopDeXuat(
                        100,
                        new BigDecimal("1.10"),
                        40));
        // ceil(110/40)=3
    }

    @Test
    void sectionsRejectsNonPositiveCap() {
        assertThrows(IllegalArgumentException.class,
                () -> DuBaoMoLopCalculator.soLopDeXuat(10, BigDecimal.ONE, 0));
    }
}
