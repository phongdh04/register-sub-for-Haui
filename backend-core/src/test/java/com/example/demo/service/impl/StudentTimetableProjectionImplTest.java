package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.StudentTimetableEntry;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.StudentTimetableEntryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Sprint 5 — Test projection TKB sinh viên: parse JSON, idempotent delete-then-insert,
 * và behaviour khi lớp chưa có lịch.
 */
@ExtendWith(MockitoExtension.class)
class StudentTimetableProjectionImplTest {

    @Mock private DangKyHocPhanRepository dangKyHocPhanRepository;
    @Mock private StudentTimetableEntryRepository entryRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private StudentTimetableProjectionImpl projection;

    @Test
    void upsert_expandsAllSlotsAndDeletesOldOnesFirst() {
        Long idDangKy = 100L;
        DangKyHocPhan dkhp = sampleDangKy(idDangKy, sampleLop(2L, "INT2204_1", List.of(
                Map.of("thu", 2, "tiet", "1-3", "phong", "A.101",
                        "ngay_bat_dau", "2024-09-10", "ngay_ket_thuc", "2024-12-10"),
                Map.of("thu", 4, "tiet", "4-6", "phong", "A.202",
                        "ngay_bat_dau", "2024-09-10", "ngay_ket_thuc", "2024-12-10")
        )));
        when(entityManager.find(DangKyHocPhan.class, idDangKy)).thenReturn(dkhp);

        int slots = projection.upsertForRegistration(idDangKy);

        assertThat(slots).isEqualTo(2);
        verify(entryRepository).deleteByIdDangKy(idDangKy);

        ArgumentCaptor<List<StudentTimetableEntry>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(entryRepository).saveAll(captor.capture());
        List<StudentTimetableEntry> rows = captor.getValue();
        assertThat(rows).hasSize(2);
        assertThat(rows).extracting(StudentTimetableEntry::getThu).containsExactly((short) 2, (short) 4);
        assertThat(rows).extracting(StudentTimetableEntry::getPhong).containsExactly("A.101", "A.202");
        assertThat(rows).extracting(StudentTimetableEntry::getSlotIndex).containsExactly((short) 0, (short) 1);
        assertThat(rows.get(0).getMaHocPhan()).isEqualTo("INT2204");
        assertThat(rows.get(0).getTenGiangVien()).isEqualTo("Nguyễn A");
    }

    @Test
    void upsert_skipsWhenScheduleJsonEmpty() {
        Long idDangKy = 101L;
        DangKyHocPhan dkhp = sampleDangKy(idDangKy, sampleLop(3L, "INT2204_2", List.of()));
        when(entityManager.find(DangKyHocPhan.class, idDangKy)).thenReturn(dkhp);

        int slots = projection.upsertForRegistration(idDangKy);

        assertThat(slots).isZero();
        verify(entryRepository).deleteByIdDangKy(idDangKy);
        verify(entryRepository, never()).saveAll(any());
    }

    @Test
    void upsert_returnsZeroWhenDangKyMissing() {
        when(entityManager.find(DangKyHocPhan.class, 999L)).thenReturn(null);

        int slots = projection.upsertForRegistration(999L);

        assertThat(slots).isZero();
        verify(entryRepository, never()).deleteByIdDangKy(anyLong());
        verify(entryRepository, never()).saveAll(any());
    }

    @Test
    void remove_callsDeleteByDangKy() {
        when(entryRepository.deleteByIdDangKy(50L)).thenReturn(3);

        int removed = projection.removeForRegistration(50L);

        assertThat(removed).isEqualTo(3);
    }

    @Test
    void rebuild_purgesAndIteratesAllRegistrations() {
        DangKyHocPhan reg1 = sampleDangKy(10L, sampleLop(1L, "AAA_1",
                List.of(Map.of("thu", 2, "tiet", "1-3", "phong", "A1"))));
        DangKyHocPhan reg2 = sampleDangKy(11L, sampleLop(2L, "BBB_1", List.of()));
        when(dangKyHocPhanRepository.findRegisteredCoursesInSemester(7L, 8L))
                .thenReturn(List.of(reg1, reg2));

        int slots = projection.rebuildForStudent(7L, 8L);

        assertThat(slots).isEqualTo(1);
        verify(entryRepository).deleteBySinhVienAndHocKy(7L, 8L);
    }

    private static DangKyHocPhan sampleDangKy(Long id, LopHocPhan lhp) {
        SinhVien sv = SinhVien.builder().idSinhVien(7L).maSinhVien("SV01").hoTen("Sinh Vien").build();
        HocKy hk = HocKy.builder().idHocKy(8L).namHoc("2024-2025").kyThu(1).build();
        return DangKyHocPhan.builder()
                .idDangKy(id)
                .sinhVien(sv)
                .lopHocPhan(lhp)
                .hocKy(hk)
                .trangThaiDangKy("THANH_CONG")
                .build();
    }

    private static LopHocPhan sampleLop(Long id, String maLopHp, List<Map<String, Object>> tkb) {
        HocPhan hp = HocPhan.builder().maHocPhan("INT2204").tenHocPhan("Lập trình hướng đối tượng").build();
        GiangVien gv = GiangVien.builder().idGiangVien(99L).tenGiangVien("Nguyễn A").build();
        return LopHocPhan.builder()
                .idLopHp(id)
                .maLopHp(maLopHp)
                .hocPhan(hp)
                .giangVien(gv)
                .siSoToiDa(50)
                .siSoThucTe(0)
                .thoiKhoaBieuJson(tkb)
                .build();
    }
}
