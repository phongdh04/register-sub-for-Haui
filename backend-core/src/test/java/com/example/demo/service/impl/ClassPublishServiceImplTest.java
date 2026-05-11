package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import com.example.demo.payload.request.LopHocPhanAssignGiangVienRequest;
import com.example.demo.payload.response.LopHocPhanBulkPublishResponse;
import com.example.demo.payload.response.LopHocPhanPublishResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.LopHocPhanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sprint 3 — workflow công bố lớp:
 * <ul>
 *   <li>assignGiangVien auto-promote SHELL → SCHEDULED khi đã có lịch.</li>
 *   <li>publish guard: thiếu GV/lịch → 422.</li>
 *   <li>bulk-publish bỏ qua các lớp không hợp lệ và đếm đúng.</li>
 * </ul>
 */
class ClassPublishServiceImplTest {

    private final LopHocPhanRepository lopHocPhanRepository = mock(LopHocPhanRepository.class);
    private final GiangVienRepository giangVienRepository = mock(GiangVienRepository.class);
    private ClassPublishServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClassPublishServiceImpl(lopHocPhanRepository, giangVienRepository);
        when(lopHocPhanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void assignGiangVien_autoPromoteSCHEDULED_khiLopCoLich() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SHELL, true, null);
        GiangVien gv = GiangVien.builder().idGiangVien(99L).tenGiangVien("GV-X").build();

        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        when(giangVienRepository.findById(99L)).thenReturn(Optional.of(gv));

        LopHocPhanAssignGiangVienRequest req = new LopHocPhanAssignGiangVienRequest();
        req.setIdGiangVien(99L);
        LopHocPhanPublishResponse resp = service.assignGiangVien(1L, req);

        assertThat(resp.getStatusPublish()).isEqualTo(LopHocPhanPublishStatus.SCHEDULED);
        assertThat(resp.getIdGiangVien()).isEqualTo(99L);
    }

    @Test
    void assignGiangVien_lopChuaCoLich_giuSHELL() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SHELL, false, null);
        GiangVien gv = GiangVien.builder().idGiangVien(99L).tenGiangVien("GV-X").build();
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        when(giangVienRepository.findById(99L)).thenReturn(Optional.of(gv));

        LopHocPhanAssignGiangVienRequest req = new LopHocPhanAssignGiangVienRequest();
        req.setIdGiangVien(99L);
        LopHocPhanPublishResponse resp = service.assignGiangVien(1L, req);
        assertThat(resp.getStatusPublish()).isEqualTo(LopHocPhanPublishStatus.SHELL);
    }

    @Test
    void assignGiangVien_lopDaPUBLISHED_traVeConflict() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.PUBLISHED, true,
                GiangVien.builder().idGiangVien(7L).build());
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));

        LopHocPhanAssignGiangVienRequest req = new LopHocPhanAssignGiangVienRequest();
        req.setIdGiangVien(99L);
        assertThatThrownBy(() -> service.assignGiangVien(1L, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("đã PUBLISHED");
    }

    @Test
    void publish_thieuGV_throw422() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SCHEDULED, true, null);
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        assertThatThrownBy(() -> service.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("chưa có giảng viên");
    }

    @Test
    void publish_thieuLich_throw422() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SCHEDULED, false,
                GiangVien.builder().idGiangVien(7L).build());
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        assertThatThrownBy(() -> service.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("thời khóa biểu");
    }

    @Test
    void publish_lopSHELL_throw422() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SHELL, true,
                GiangVien.builder().idGiangVien(7L).build());
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        assertThatThrownBy(() -> service.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("SHELL");
    }

    @Test
    void publish_lopHopLe_chuyenPUBLISHED() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.SCHEDULED, true,
                GiangVien.builder().idGiangVien(7L).tenGiangVien("Cô A").build());
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));

        LopHocPhanPublishResponse resp = service.publish(1L);
        assertThat(resp.getStatusPublish()).isEqualTo(LopHocPhanPublishStatus.PUBLISHED);
    }

    @Test
    void publish_lopDaPublished_idempotent() {
        LopHocPhan lhp = newLhp(1L, "MA1", LopHocPhanPublishStatus.PUBLISHED, true,
                GiangVien.builder().idGiangVien(7L).build());
        when(lopHocPhanRepository.findById(1L)).thenReturn(Optional.of(lhp));
        LopHocPhanPublishResponse resp = service.publish(1L);
        assertThat(resp.getStatusPublish()).isEqualTo(LopHocPhanPublishStatus.PUBLISHED);
        assertThat(resp.getMessage()).contains("đã PUBLISHED");
    }

    @Test
    void bulkPublish_thanhCongVaSkipDungCach() {
        LopHocPhan ok = newLhp(1L, "OK1", LopHocPhanPublishStatus.SCHEDULED, true,
                GiangVien.builder().idGiangVien(7L).build());
        LopHocPhan thieuGv = newLhp(2L, "BAD1", LopHocPhanPublishStatus.SCHEDULED, true, null);
        LopHocPhan thieuLich = newLhp(3L, "BAD2", LopHocPhanPublishStatus.SCHEDULED, false,
                GiangVien.builder().idGiangVien(7L).build());

        when(lopHocPhanRepository.findByHocKyAndStatusPublish(eq(10L), eq(LopHocPhanPublishStatus.SCHEDULED)))
                .thenReturn(List.of(ok, thieuGv, thieuLich));

        LopHocPhanBulkPublishResponse resp = service.bulkPublish(10L);
        assertThat(resp.getTotalRequested()).isEqualTo(3);
        assertThat(resp.getPublishedCount()).isEqualTo(1);
        assertThat(resp.getSkippedCount()).isEqualTo(2);
        assertThat(resp.getPublishedIds()).containsExactly(1L);
        assertThat(resp.getSkipped())
                .extracting(LopHocPhanBulkPublishResponse.SkippedLopHocPhan::getMaLopHp)
                .containsExactlyInAnyOrder("BAD1", "BAD2");
    }

    // ----- factory -----

    private static LopHocPhan newLhp(Long id, String maLopHp,
                                     LopHocPhanPublishStatus status,
                                     boolean withSchedule,
                                     GiangVien gv) {
        return LopHocPhan.builder()
                .idLopHp(id)
                .maLopHp(maLopHp)
                .hocKy(HocKy.builder().idHocKy(10L).kyThu(1).namHoc("2025-2026").build())
                .hocPhan(HocPhan.builder().idHocPhan(100L).maHocPhan("HP100").tenHocPhan("Tên HP").build())
                .giangVien(gv)
                .siSoToiDa(40)
                .siSoThucTe(0)
                .statusPublish(status)
                .thoiKhoaBieuJson(withSchedule ? List.of(Map.of("thu", 2, "tiet", "1-3")) : List.of())
                .build();
    }
}
