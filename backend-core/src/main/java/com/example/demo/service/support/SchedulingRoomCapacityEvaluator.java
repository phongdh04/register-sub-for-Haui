package com.example.demo.service.support;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.response.SchedulingConflictItemResponse;
import com.example.demo.scheduling.snapshot.SchedulingOccupancyOccurrence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * So sánh Σ(sĩ số các lớp chồng tiết cùng phòng/thứ với {@code suc_chua} — BACK-TKB-012 subset.
 */
public final class SchedulingRoomCapacityEvaluator {

    private SchedulingRoomCapacityEvaluator() {
    }

    /** @param excludedSelfLhpId lớp đang draft (không đếm bản đã ghim trong chỉ mục với overlap chính nó sau exclude). */
    public static List<SchedulingConflictItemResponse> evaluatePhongOverload(
            long idPhongHoc,
            int sucChua,
            String roomIndexKey,
            int tietBd,
            int tietKt,
            long excludedSelfLhpId,
            int draftSiSoToiDa,
            Map<String, List<SchedulingOccupancyOccurrence>> roomOccupancyByKey,
            Map<Long, LopHocPhan> lhpById) {

        int sumSiSo = draftSiSoToiDa;
        List<SchedulingOccupancyOccurrence> occ = roomOccupancyByKey.get(roomIndexKey);
        if (occ != null) {
            for (SchedulingOccupancyOccurrence o : occ) {
                if (o.idLopHp() == excludedSelfLhpId) {
                    continue;
                }
                if (!o.overlaps(tietBd, tietKt)) {
                    continue;
                }
                LopHocPhan other = lhpById.get(o.idLopHp());
                sumSiSo += other != null && other.getSiSoToiDa() != null ? other.getSiSoToiDa() : 0;
            }
        }

        List<SchedulingConflictItemResponse> r = new ArrayList<>();
        if (sumSiSo > sucChua) {
            r.add(SchedulingConflictItemResponse.builder()
                    .conflictType("PHONG_KHONG_DU_SUC_CHUA")
                    .conflictingLopHpId(null)
                    .detail("Σ sĩ số chồng tiết=" + sumSiSo + " > suc_chua=" + sucChua + " cho phòng id=" + idPhongHoc + " key=" + roomIndexKey)
                    .build());
        }
        return r;
    }
}
