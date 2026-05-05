package com.example.demo.scheduling;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.scheduling.snapshot.SchedulingIndexData;
import com.example.demo.scheduling.snapshot.SchedulingOccupancyOccurrence;
import com.example.demo.service.SchedulingSnapshotIndexBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BK-TKB-012 — chồng phòng và loại trừ chính LHP trong conflict-check. */
class SchedulingSnapshotOverlapTest {

    @Test
    void occupancy_overlapMatchesAdjacent() {
        var a = new SchedulingOccupancyOccurrence(1L, 1, 3);
        assertTrue(a.overlaps(3, 4));
        assertTrue(a.overlaps(2, 2));
        assertFalse(a.overlaps(4, 5));
    }

    @Test
    void indexBuilder_sameRoomOverlappingAddsTwoOccurrences_underDifferentSlots() {
        PhongHoc ph = PhongHoc.builder().idPhong(99L).maPhong("X1").build();
        LopHocPhan a = baseLhp(10L, ph, null, List.of(Map.of("thu", 2, "tiet", "1-2")));
        LopHocPhan b = baseLhp(11L, ph, null, List.of(Map.of("thu", 2, "tiet", "2-3")));
        var idx = SchedulingSnapshotIndexBuilder.buildFromLhps(List.of(a, b));
        String key = SchedulingIndexData.roomKey(99L, null, 2);
        List<SchedulingOccupancyOccurrence> occ = idx.roomOccupancyByKey().get(key);
        assertEquals(2, occ.size());
    }

    @Test
    void conflictCheckExcludesSelfRoom_onlyOtherLhpCounts() {
        PhongHoc ph = PhongHoc.builder().idPhong(7L).maPhong("A101").build();
        LopHocPhan self = baseLhp(5L, ph, null, List.of(Map.of("thu", 3, "tiet", "1-2")));
        LopHocPhan other = baseLhp(6L, ph, null, List.of(Map.of("thu", 3, "tiet", "3-4")));
        var idx = SchedulingSnapshotIndexBuilder.buildFromLhps(List.of(self, other));
        String key = SchedulingIndexData.roomKey(7L, null, 3);
        int conflictsExcludingSelf = 0;
        for (SchedulingOccupancyOccurrence occ : idx.roomOccupancyByKey().getOrDefault(key, List.of())) {
            if (occ.idLopHp() == 5L) {
                continue;
            }
            if (occ.overlaps(1, 2)) {
                conflictsExcludingSelf++;
            }
        }
        assertEquals(0, conflictsExcludingSelf);

        int conflictsWithOverlap = 0;
        for (SchedulingOccupancyOccurrence occ : idx.roomOccupancyByKey().getOrDefault(key, List.of())) {
            if (occ.idLopHp() == 5L) {
                continue;
            }
            if (occ.overlaps(2, 3)) {
                conflictsWithOverlap++;
            }
        }
        assertEquals(1, conflictsWithOverlap);
    }

    @Test
    void gvIndexUsesEntityFallbackWhenJsonMissingGv() {
        PhongHoc ph = PhongHoc.builder().idPhong(1L).maPhong("P1").build();
        GiangVien gv = GiangVien.builder().idGiangVien(42L).tenGiangVien("GV X").build();
        LopHocPhan a = baseLhp(1L, ph, gv, List.of(Map.of("thu", 4, "tiet", "1-1")));
        var idx = SchedulingSnapshotIndexBuilder.buildFromLhps(List.of(a));
        String gk = SchedulingIndexData.gvKey(42L, 4);
        assertTrue(idx.gvOccupancyByKey().containsKey(gk));
    }

    private static LopHocPhan baseLhp(long id, PhongHoc ph, GiangVien gv, List<Map<String, Object>> json) {
        HocKy hk = HocKy.builder().idHocKy(1L).namHoc("2024-2025").kyThu(1).build();
        HocPhan hp = HocPhan.builder()
                .idHocPhan(100L + id)
                .maHocPhan("HP_" + id)
                .tenHocPhan("Mon " + id)
                .soTinChi(3)
                .build();
        return LopHocPhan.builder()
                .idLopHp(id)
                .maLopHp("LHP_" + id)
                .hocKy(hk)
                .hocPhan(hp)
                .phongHoc(ph)
                .giangVien(gv)
                .siSoToiDa(40)
                .thoiKhoaBieuJson(json)
                .build();
    }
}
