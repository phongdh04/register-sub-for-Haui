package com.example.demo.support;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.RegistrationWindow;
import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.repository.RegistrationWindowRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sprint 1 — kiểm tra resolver của {@link RegistrationScheduleChecker}.
 *
 * <p>Nguồn dữ liệu runtime: {@link RegistrationWindowRepository#findByHocKyAndPhaseOrdered} + lọc scope.
 */
class RegistrationScheduleCheckerTest {

    private final RegistrationWindowRepository repo = mock(RegistrationWindowRepository.class);
    private final RegistrationScheduleChecker checker = new RegistrationScheduleChecker(repo);

    @Test
    void pre_khongCoWindow_thiDong_duCoMoTrenBangHocKy() {
        Instant now = Instant.now();
        HocKy hk = HocKy.builder()
                .idHocKy(1L)
                .preDangKyMoTu(now.minus(1, ChronoUnit.HOURS))
                .preDangKyMoDen(now.plus(1, ChronoUnit.HOURS))
                .build();
        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.PRE)))
                .thenReturn(List.of());

        assertThat(checker.isPreRegistrationOpenFor(hk, 2021, 5L)).isFalse();
    }

    @Test
    void official_khongCoWindowVaKhongCoMocHocKy_thiDong() {
        HocKy hk = HocKy.builder().idHocKy(1L).build();
        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.OFFICIAL))).thenReturn(List.of());
        assertThat(checker.isOfficialRegistrationOpenFor(hk, 2021, 5L)).isFalse();
    }

    @Test
    void window_cohortDangMo_traVeTrue() {
        Instant now = Instant.now();
        RegistrationWindow w = RegistrationWindow.builder()
                .openAt(now.minus(30, ChronoUnit.MINUTES))
                .closeAt(now.plus(30, ChronoUnit.MINUTES))
                .phase(RegistrationPhase.OFFICIAL)
                .namNhapHoc(2021)
                .build();
        HocKy hk = HocKy.builder().idHocKy(1L).build();

        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.OFFICIAL))).thenReturn(List.of(w));

        assertThat(checker.isOfficialRegistrationOpenFor(hk, 2021, 5L)).isTrue();
    }

    @Test
    void window_daDong_thiTraFalse() {
        Instant now = Instant.now();
        RegistrationWindow specific = RegistrationWindow.builder()
                .openAt(now.minus(2, ChronoUnit.HOURS))
                .closeAt(now.minus(1, ChronoUnit.HOURS))
                .phase(RegistrationPhase.OFFICIAL)
                .namNhapHoc(2021)
                .build();
        HocKy hk = HocKy.builder()
                .idHocKy(1L)
                .dangKyChinhThucTu(now.minus(1, ChronoUnit.DAYS))
                .dangKyChinhThucDen(now.plus(1, ChronoUnit.DAYS))
                .build();

        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.OFFICIAL)))
                .thenReturn(List.of(specific));

        assertThat(checker.isOfficialRegistrationOpenFor(hk, 2021, 5L)).isFalse();
    }

    @Test
    void window_chuaToiOpen_traFalse() {
        Instant now = Instant.now();
        RegistrationWindow w = RegistrationWindow.builder()
                .openAt(now.plus(1, ChronoUnit.HOURS))
                .closeAt(now.plus(2, ChronoUnit.HOURS))
                .phase(RegistrationPhase.PRE)
                .namNhapHoc(2021)
                .build();
        HocKy hk = HocKy.builder().idHocKy(1L).build();
        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.PRE))).thenReturn(List.of(w));

        assertThat(checker.isPreRegistrationOpenFor(hk, 2021, 5L)).isFalse();
    }

    /**
     * Đã xóa cửa sổ PRE + không có mốc trên hoc_ky → PRE đóng (không còn mặc định «mở»).
     */
    @Test
    void pre_khongCoWindowVaKhongCoMocHocKy_thiDong() {
        HocKy hk = HocKy.builder().idHocKy(1L).build();
        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.PRE))).thenReturn(List.of());
        assertThat(checker.isPreRegistrationOpen(hk)).isFalse();
    }

    @Test
    void window_nganhSpecific_uuTienHonCohortChung() {
        Instant now = Instant.now();
        RegistrationWindow nganhSpecific = RegistrationWindow.builder()
                .openAt(now.minus(10, ChronoUnit.MINUTES))
                .closeAt(now.plus(10, ChronoUnit.MINUTES))
                .phase(RegistrationPhase.OFFICIAL)
                .namNhapHoc(2021)
                .nganhDaoTao(NganhDaoTao.builder().idNganh(5L).build())
                .build();
        RegistrationWindow cohortOnly = RegistrationWindow.builder()
                .openAt(now.minus(30, ChronoUnit.MINUTES))
                .closeAt(now.plus(30, ChronoUnit.MINUTES))
                .phase(RegistrationPhase.OFFICIAL)
                .namNhapHoc(2021)
                .build();

        HocKy hk = HocKy.builder().idHocKy(1L).build();

        when(repo.findByHocKyAndPhaseOrdered(eq(1L), eq(RegistrationPhase.OFFICIAL)))
                .thenReturn(List.of(nganhSpecific, cohortOnly));

        assertThat(checker.isOfficialRegistrationOpenFor(hk, 2021, 5L)).isTrue();
    }
}
