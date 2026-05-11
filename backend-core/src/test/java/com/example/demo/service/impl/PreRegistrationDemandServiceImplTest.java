package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.payload.response.PreRegistrationDemandItemResponse;
import com.example.demo.payload.response.PreRegistrationDemandResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sprint 2 — kiểm tra tính {@code recommendedClasses} và tổng hợp demand.
 */
class PreRegistrationDemandServiceImplTest {

    private final PreRegistrationIntentRepository intentRepository = mock(PreRegistrationIntentRepository.class);
    private final HocKyRepository hocKyRepository = mock(HocKyRepository.class);
    private final NganhDaoTaoRepository nganhDaoTaoRepository = mock(NganhDaoTaoRepository.class);

    private PreRegistrationDemandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PreRegistrationDemandServiceImpl(intentRepository, hocKyRepository, nganhDaoTaoRepository);
        ReflectionTestUtils.setField(service, "defaultTargetClassSize", 40);
    }

    @Test
    void aggregate_quayLoiKhiHocKyKhongTonTai() {
        when(hocKyRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.aggregate(1L, null, null, null))
                .hasMessageContaining("Không tìm thấy học kỳ");
    }

    @Test
    void aggregate_tinhRecommendedClassesTheoCeil() {
        HocKy hk = HocKy.builder().idHocKy(1L).kyThu(1).namHoc("2025-2026").build();
        when(hocKyRepository.findById(1L)).thenReturn(Optional.of(hk));

        // 2 học phần: HP1 = 81 intent, HP2 = 40 intent, classSize 40 → 3 lớp + 1 lớp
        when(intentRepository.aggregateDemand(eq(1L), any(), any())).thenReturn(List.<Object[]>of(
                new Object[]{10L, "HP1", "Học phần 1", 3, 2021, 5L, "Ngành A", 81L},
                new Object[]{20L, "HP2", "Học phần 2", 4, 2021, 5L, "Ngành A", 40L}
        ));

        PreRegistrationDemandResponse resp = service.aggregate(1L, null, null, null);

        assertThat(resp.getTargetClassSize()).isEqualTo(40);
        assertThat(resp.getTotalIntents()).isEqualTo(121);
        assertThat(resp.getTotalRecommendedClasses()).isEqualTo(4);
        assertThat(resp.getItems()).extracting(PreRegistrationDemandItemResponse::getRecommendedClasses)
                .containsExactly(3, 1);
    }

    @Test
    void aggregate_demandRongTraVe0LopVa0Intent() {
        HocKy hk = HocKy.builder().idHocKy(2L).kyThu(2).namHoc("2025-2026").build();
        when(hocKyRepository.findById(2L)).thenReturn(Optional.of(hk));
        when(intentRepository.aggregateDemand(any(), any(), any())).thenReturn(List.of());

        PreRegistrationDemandResponse resp = service.aggregate(2L, null, null, null);

        assertThat(resp.getItems()).isEmpty();
        assertThat(resp.getTotalIntents()).isZero();
        assertThat(resp.getTotalRecommendedClasses()).isZero();
    }

    @Test
    void aggregate_targetClassSizeCustom_duocTonTrong() {
        HocKy hk = HocKy.builder().idHocKy(1L).kyThu(1).namHoc("2025-2026").build();
        when(hocKyRepository.findById(1L)).thenReturn(Optional.of(hk));
        when(intentRepository.aggregateDemand(any(), any(), any())).thenReturn(List.<Object[]>of(
                new Object[]{10L, "HP1", "Học phần 1", 3, 2021, 5L, "Ngành A", 100L}
        ));
        // classSize = 30 → ceil(100/30) = 4
        PreRegistrationDemandResponse resp = service.aggregate(1L, null, null, 30);
        assertThat(resp.getTargetClassSize()).isEqualTo(30);
        assertThat(resp.getItems().get(0).getRecommendedClasses()).isEqualTo(4);
    }

    @Test
    void aggregate_demandToiThieuVan1Lop() {
        HocKy hk = HocKy.builder().idHocKy(1L).kyThu(1).namHoc("2025-2026").build();
        when(hocKyRepository.findById(1L)).thenReturn(Optional.of(hk));
        when(intentRepository.aggregateDemand(any(), any(), any())).thenReturn(List.<Object[]>of(
                new Object[]{10L, "HP1", "Học phần 1", 3, 2021, 5L, "Ngành A", 1L}
        ));
        PreRegistrationDemandResponse resp = service.aggregate(1L, null, null, null);
        assertThat(resp.getItems().get(0).getRecommendedClasses()).isEqualTo(1);
    }
}
