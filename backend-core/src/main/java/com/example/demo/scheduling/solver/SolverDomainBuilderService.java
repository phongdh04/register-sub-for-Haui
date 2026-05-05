package com.example.demo.scheduling.solver;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.enums.TrangThaiPhong;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.GvBusySlotRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
/**
 * BACK-TKB-020 — vật chất hoá tuple (R,G,S) đã được bóp bằng filter thô.
 *
 * <p>Hạn chế MVP: nếu LHP có nhiều GV dự phòng thì chỉ trừ busy theo <em>GV đầu danh sách</em>
 * để ước lượng kích thước domain.</p>
 */
@Service
@RequiredArgsConstructor
public class SolverDomainBuilderService {

    private final PhongHocRepository phongHocRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final GiangVienRepository giangVienRepository;
    private final GvBusySlotRepository gvBusySlotRepository;
    private final SolverCpSatProperties solverCpSatProperties;

    public SolverDomainBundle build(SolverRunRequest request) {
        Long hkId = request.hocKyId();
        SolverScope scope = request.scope();

        List<PhongHoc> roomsHoatDong = phongHocRepository.findByTrangThai(TrangThaiPhong.HOAT_DONG);
        List<Long> roomIds = roomsHoatDong.stream().map(PhongHoc::getIdPhong).distinct().sorted().toList();

        List<LopHocPhan> rawLhps = lopHocPhanRepository.findByHocKy_IdHocKy(hkId).stream()
                .filter(SolverDomainBuilderService::isSchedulableLhp)
                .toList();

        LinkedHashSet<Long> gvInHkSet = new LinkedHashSet<>();
        for (LopHocPhan l : rawLhps) {
            if (l.getGiangVien() != null && l.getGiangVien().getIdGiangVien() != null) {
                gvInHkSet.add(l.getGiangVien().getIdGiangVien());
            }
        }
        List<Long> gvIdsDistinct = gvInHkSet.stream().sorted().toList();

        List<SolverAtomicSlot> baseSlots =
                SolverSlotEnumeration.allAtomicSlots(solverCpSatProperties.getMaxTietPerDay());

        List<SolverLhpSubdomain> subs = new ArrayList<>();
        long totalTuples = 0;
        PageRequest gvPoolCap = PageRequest.of(0, Math.max(1, solverCpSatProperties.getLecturerPoolCap()));
        List<Long> gvPoolIds = giangVienRepository.findAll(gvPoolCap).stream()
                .map(GiangVien::getIdGiangVien)
                .toList();

        for (LopHocPhan lhp : rawLhps) {
            Integer cap = lhp.getSiSoToiDa();
            List<Long> candRooms = roomsHoatDong.stream()
                    .filter(p -> cap == null || (p.getSucChua() != null && p.getSucChua() >= cap))
                    .map(PhongHoc::getIdPhong)
                    .toList();

            List<Long> candGvs;
            if (lhp.getGiangVien() != null && lhp.getGiangVien().getIdGiangVien() != null) {
                candGvs = List.of(lhp.getGiangVien().getIdGiangVien());
            } else {
                candGvs = gvPoolIds;
            }

            long gvProbe = candGvs.isEmpty() ? -1 : candGvs.getFirst();
            List<GvBusySlot> busy = gvProbe > 0
                    ? gvBusySlotRepository.findForSchedulingView(gvProbe, hkId)
                    : List.of();

            List<SolverAtomicSlot> candSlots =
                    gvProbe > 0 ? SolverGvBusyFilter.filterHardBusy(baseSlots, busy)
                            : SolverGvBusyFilter.filterHardBusy(baseSlots, List.of());

            long tuple = safeTripleProduct(candRooms.size(), candGvs.size(), candSlots.size());
            totalTuples = cappedAdd(totalTuples, tuple);

            subs.add(new SolverLhpSubdomain(
                    lhp.getIdLopHp(),
                    lhp.getMaLopHp(),
                    candRooms,
                    candGvs,
                    candSlots,
                    tuple));
        }

        totalTuples = cappedSaturate(totalTuples);

        return new SolverDomainBundle(
                hkId,
                scope,
                roomIds,
                gvIdsDistinct,
                List.copyOf(baseSlots),
                List.copyOf(subs),
                totalTuples);
    }

    private static boolean isSchedulableLhp(LopHocPhan l) {
        if (l == null) {
            return false;
        }
        String tt = l.getTrangThai();
        return tt == null || !"DA_HUY".equalsIgnoreCase(tt);
    }

    private static long safeTripleProduct(long r, long g, long s) {
        try {
            return Math.multiplyExact(Math.multiplyExact(Math.max(r, 0), Math.max(g, 0)), Math.max(s, 0));
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    private static long cappedAdd(long cur, long add) {
        try {
            return Math.addExact(cur, add);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    private static long cappedSaturate(long v) {
        return v <= 0 ? 0 : v;
    }
}
