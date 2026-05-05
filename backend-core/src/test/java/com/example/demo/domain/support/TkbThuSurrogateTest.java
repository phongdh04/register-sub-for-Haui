package com.example.demo.domain.support;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TkbThuSurrogateTest {

    @Test
    void extractNumberFromFirstSlot() {
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("thu", 4);
        assertEquals((short) 4, TkbThuSurrogate.extractThuFromFirstSlot(List.of(slot)));
    }

    @Test
    void extractStringThu() {
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("thu", "8");
        assertEquals((short) 8, TkbThuSurrogate.extractThuFromFirstSlot(List.of(slot)));
    }

    @Test
    void rejectsOutOfRange() {
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("thu", 1);
        assertNull(TkbThuSurrogate.extractThuFromFirstSlot(List.of(slot)));
    }

    @Test
    void nullWhenEmptyJson() {
        assertNull(TkbThuSurrogate.extractThuFromFirstSlot(null));
        assertNull(TkbThuSurrogate.extractThuFromFirstSlot(List.of()));
    }
}
