package com.example.demo.service;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.scheduling.snapshot.SchedulingIndexData;
import com.example.demo.scheduling.snapshot.SchedulingOccupancyOccurrence;
import com.example.demo.service.support.SchedulingJsonSlotNormalizer;
import com.example.demo.service.support.SchedulingJsonSlotNormalizer.ParsedTiet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Xây chỉ mục (phòng+thứ, GV+thứ) từ LHP + JSON.
 * Ưu tiên {@code id_phong_hoc}/{@code id_giang_vien} trên entity khi JSON không mang FK đủ.
 */
public final class SchedulingSnapshotIndexBuilder {

    private SchedulingSnapshotIndexBuilder() {
    }

    public static SchedulingIndexData buildFromLhps(List<LopHocPhan> lhps) {
        SchedulingIndexData data = SchedulingIndexData.emptyMutable();
        if (lhps == null) {
            return data;
        }
        for (LopHocPhan lhp : lhps) {
            List<Map<String, Object>> slots = SchedulingJsonSlotNormalizer.slotsMapsFromStoredJson(lhp.getThoiKhoaBieuJson());
            if (slots.isEmpty()) {
                continue;
            }
            for (Map<String, Object> slot : slots) {
                accumulateSlot(data, lhp, slot);
            }
        }
        return data;
    }

    private static void accumulateSlot(SchedulingIndexData data, LopHocPhan lhp, Map<String, Object> slot) {
        Integer thu = SchedulingJsonSlotNormalizer.coercibleThuInt(slot);
        ParsedTiet tiet = SchedulingJsonSlotNormalizer.parseTiet(slot);
        if (thu == null || !tiet.present()) {
            return;
        }
        Long roomFkSlot = SchedulingJsonSlotNormalizer.resolvePhongIdFk(slot);
        Long roomFkEffective = roomFkSlot != null ? roomFkSlot : fkPhongLhp(lhp);
        String legacy = SchedulingJsonSlotNormalizer.normalizedLegacyPhong(slot);

        Long gvSlot = SchedulingJsonSlotNormalizer.resolveGiangVienIdFk(slot);
        Long gvEffective = gvSlot != null ? gvSlot : fkGiangVienLhp(lhp);

        if (roomFkEffective != null) {
            addOccupancy(data.roomOccupancyByKey(),
                    SchedulingIndexData.roomKey(roomFkEffective, null, thu),
                    lhp.getIdLopHp(), tiet.bd(), tiet.kt());
        } else if (!legacy.isEmpty()) {
            addOccupancy(data.roomOccupancyByKey(),
                    SchedulingIndexData.roomKey(null, legacy, thu),
                    lhp.getIdLopHp(), tiet.bd(), tiet.kt());
        }

        if (gvEffective != null) {
            addOccupancy(data.gvOccupancyByKey(),
                    SchedulingIndexData.gvKey(gvEffective, thu),
                    lhp.getIdLopHp(), tiet.bd(), tiet.kt());
        }
    }

    private static Long fkPhongLhp(LopHocPhan lhp) {
        PhongHoc ph = lhp.getPhongHoc();
        return ph != null ? ph.getIdPhong() : null;
    }

    private static Long fkGiangVienLhp(LopHocPhan lhp) {
        GiangVien gv = lhp.getGiangVien();
        return gv != null ? gv.getIdGiangVien() : null;
    }

    static void addOccupancy(Map<String, List<SchedulingOccupancyOccurrence>> idx, String key, long idLopHp, int bd, int kt) {
        idx.computeIfAbsent(key, k -> new ArrayList<>()).add(new SchedulingOccupancyOccurrence(idLopHp, bd, kt));
    }
}
