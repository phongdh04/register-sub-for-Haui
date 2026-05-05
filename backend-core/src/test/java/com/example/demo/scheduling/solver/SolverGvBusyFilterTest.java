package com.example.demo.scheduling.solver;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.enums.GvBusyLoai;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SolverGvBusyFilterTest {

    @Test
    void removesAtomicSlotsOverlappingHardBusy_sameThursday() {
        List<SolverAtomicSlot> base = List.of(new SolverAtomicSlot(2, 3), new SolverAtomicSlot(2, 4));
        GiangVien gv = new GiangVien();
        gv.setIdGiangVien(1L);
        HocKy hk = new HocKy();
        hk.setIdHocKy(99L);

        GvBusySlot busy = GvBusySlot.builder()
                .giangVien(gv)
                .hocKy(hk)
                .thu((short) 2)
                .tietBd((short) 3)
                .tietKt((short) 3)
                .loai(GvBusyLoai.HARD)
                .build();

        List<SolverAtomicSlot> filtered = SolverGvBusyFilter.filterHardBusy(base, List.of(busy));
        Assertions.assertEquals(List.of(new SolverAtomicSlot(2, 4)), filtered);
    }
}
