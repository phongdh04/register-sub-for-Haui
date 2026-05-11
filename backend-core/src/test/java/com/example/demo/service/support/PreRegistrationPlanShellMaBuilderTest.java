package com.example.demo.service.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PreRegistrationPlanShellMaBuilderTest {

    @Test
    void lengthNeverExceedsDbMax() {
        String m = assertDoesNotThrow(() -> PreRegistrationPlanShellMaBuilder.build(
                "VeryLongMaHocPhanNameThatShouldBeClipped", "a1b2c3d4", 12));
        assertThat(m.length()).isLessThanOrEqualTo(PreRegistrationPlanShellMaBuilder.MA_LOP_HP_MAX_LEN);
    }

    @Test
    void stableForSameInputs() {
        String a = PreRegistrationPlanShellMaBuilder.build("IT6005", "deadbeef", 1);
        String b = PreRegistrationPlanShellMaBuilder.build("IT6005", "deadbeef", 1);
        assertThat(a).isEqualTo(b);
    }
}
