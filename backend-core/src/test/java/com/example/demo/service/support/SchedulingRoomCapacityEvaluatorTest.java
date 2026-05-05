package com.example.demo.service.support;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.response.SchedulingConflictItemResponse;
import com.example.demo.scheduling.snapshot.SchedulingIndexData;
import com.example.demo.scheduling.snapshot.SchedulingOccupancyOccurrence;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulingRoomCapacityEvaluatorTest {

    @Test
    void reportsOverload_whenSumSiSoExceedsSuc_chua_despiteExclusiveSlotGeometry() {
        String key = SchedulingIndexData.roomKey(1L, null, 2);

        Map<String, List<SchedulingOccupancyOccurrence>> roomIx = new HashMap<>();
        roomIx.computeIfAbsent(key, k -> new ArrayList<>()).add(new SchedulingOccupancyOccurrence(99L, 4, 5));

        Map<Long, LopHocPhan> byId = new HashMap<>();
        byId.put(99L, LopHocPhan.builder().idLopHp(99L).siSoToiDa(51).build());

        List<SchedulingConflictItemResponse> v = SchedulingRoomCapacityEvaluator.evaluatePhongOverload(
                1L,
                100,
                key,
                4, 4,
                7L,
                50,
                roomIx,
                byId);

        assertEquals(1, v.size());
        assertEquals("PHONG_KHONG_DU_SUC_CHUA", v.getFirst().getConflictType());
        assertTrue(v.getFirst().getDetail().contains("101"));
    }
}
