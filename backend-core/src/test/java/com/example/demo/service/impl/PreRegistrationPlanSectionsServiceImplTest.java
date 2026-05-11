package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import com.example.demo.payload.request.PreRegistrationPlanSectionsRequest;
import com.example.demo.payload.response.PreRegistrationPlanSectionsResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.service.LopHocPhongDualWriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreRegistrationPlanSectionsServiceImplTest {

    @Mock
    private PreRegistrationIntentRepository intentRepository;
    @Mock
    private HocKyRepository hocKyRepository;
    @Mock
    private HocPhanRepository hocPhanRepository;
    @Mock
    private LopHocPhanRepository lopHocPhanRepository;
    @Mock
    private LopHocPhongDualWriteService lopHocPhongDualWriteService;
    @Mock
    private ITkbRevisionService tkbRevisionService;

    @InjectMocks
    private PreRegistrationPlanSectionsServiceImpl service;

    private final HocKy hk = HocKy.builder().idHocKy(10L).build();
    private final HocPhan hp = HocPhan.builder().idHocPhan(99L).maHocPhan("IT6005").build();

    @BeforeEach
    void setDefaultClassSize() {
        ReflectionTestUtils.setField(service, "defaultTargetClassSize", 40);
    }

    @Test
    void recommended_createsThreeSections_when180IntentsAndSize60() {
        Object[] row = new Object[]{99L, "IT6005", "X", 3, 2021, 5L, "CNTT", 180L};
        when(hocKyRepository.findById(10L)).thenReturn(Optional.of(hk));
        when(hocPhanRepository.findById(99L)).thenReturn(Optional.of(hp));
        when(intentRepository.aggregateDemand(10L, null, null)).thenReturn(Collections.singletonList(row));
        when(lopHocPhanRepository.existsByMaLopHp(any())).thenReturn(false);

        AtomicLong idSeq = new AtomicLong(1000);
        when(lopHocPhanRepository.save(any())).thenAnswer(inv -> {
            LopHocPhan l = inv.getArgument(0);
            l.setIdLopHp(idSeq.getAndIncrement());
            return l;
        });

        PreRegistrationPlanSectionsRequest req = PreRegistrationPlanSectionsRequest.builder()
                .hocKyId(10L)
                .idHocPhan(99L)
                .targetClassSize(60)
                .build();

        PreRegistrationPlanSectionsResponse res = service.planSections(req, null);

        assertThat(res.getSectionCountPlanned()).isEqualTo(3);
        assertThat(res.getTotalIntentSnapshot()).isEqualTo(180);
        assertThat(res.getRecommendedFromDemand()).isEqualTo(3);
        assertThat(res.getCreatedCount()).isEqualTo(3);
        assertThat(res.isIdempotentReplay()).isFalse();
        assertThat(res.getCreatedLopHpIds()).hasSize(3);
        verify(tkbRevisionService).bumpAfterTkbMutation(10L);

        ArgumentCaptor<LopHocPhan> cap = ArgumentCaptor.forClass(LopHocPhan.class);
        verify(lopHocPhanRepository, times(3)).save(cap.capture());
        assertThat(cap.getAllValues()).allMatch(l ->
                l.getStatusPublish() == LopHocPhanPublishStatus.SHELL
                        && "CHUA_MO".equals(l.getTrangThai())
                        && l.getSiSoToiDa() == 60);
    }

    @Test
    void explicitSectionCount_doesNotRequireIntent() {
        when(hocKyRepository.findById(10L)).thenReturn(Optional.of(hk));
        when(hocPhanRepository.findById(99L)).thenReturn(Optional.of(hp));
        when(lopHocPhanRepository.existsByMaLopHp(any())).thenReturn(false);
        when(lopHocPhanRepository.save(any())).thenAnswer(inv -> {
            LopHocPhan l = inv.getArgument(0);
            l.setIdLopHp(1L);
            return l;
        });

        PreRegistrationPlanSectionsRequest req = PreRegistrationPlanSectionsRequest.builder()
                .hocKyId(10L)
                .idHocPhan(99L)
                .sectionCount(2)
                .useRecommendedFromDemand(false)
                .build();

        PreRegistrationPlanSectionsResponse res = service.planSections(req, "k1");
        assertThat(res.getSectionCountPlanned()).isEqualTo(2);
        assertThat(res.getTotalIntentSnapshot()).isZero();
        verify(intentRepository, never()).aggregateDemand(anyLong(), any(), any());
    }

    @Test
    void replay_whenAllMasAlreadyExist() {
        Object[] row = new Object[]{99L, "IT6005", "X", 3, null, null, null, 100L};
        when(hocKyRepository.findById(10L)).thenReturn(Optional.of(hk));
        when(hocPhanRepository.findById(99L)).thenReturn(Optional.of(hp));
        when(intentRepository.aggregateDemand(10L, null, null)).thenReturn(Collections.singletonList(row));
        when(lopHocPhanRepository.existsByMaLopHp(any())).thenReturn(true);
        AtomicLong lid = new AtomicLong(700);
        when(lopHocPhanRepository.findByMaLopHp(any())).thenAnswer(inv ->
                Optional.of(LopHocPhan.builder()
                        .idLopHp(lid.getAndIncrement())
                        .maLopHp(inv.getArgument(0))
                        .build()));

        PreRegistrationPlanSectionsRequest req = PreRegistrationPlanSectionsRequest.builder()
                .hocKyId(10L)
                .idHocPhan(99L)
                .targetClassSize(50)
                .build();

        PreRegistrationPlanSectionsResponse res = service.planSections(req, "same-key");
        assertThat(res.isIdempotentReplay()).isTrue();
        assertThat(res.getCreatedCount()).isZero();
        assertThat(res.getSkippedExistingMaCount()).isEqualTo(2);
        verify(lopHocPhanRepository, never()).save(any());
        verify(tkbRevisionService, never()).bumpAfterTkbMutation(anyLong());
    }

    @Test
    void partialExistingMa_throwsConflict() {
        Object[] row = new Object[]{99L, "IT6005", "X", 3, null, null, null, 100L};
        when(hocKyRepository.findById(10L)).thenReturn(Optional.of(hk));
        when(hocPhanRepository.findById(99L)).thenReturn(Optional.of(hp));
        when(intentRepository.aggregateDemand(10L, null, null)).thenReturn(Collections.singletonList(row));
        when(lopHocPhanRepository.existsByMaLopHp(any()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        PreRegistrationPlanSectionsRequest req = PreRegistrationPlanSectionsRequest.builder()
                .hocKyId(10L)
                .idHocPhan(99L)
                .targetClassSize(50)
                .build();

        assertThatThrownBy(() -> service.planSections(req, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(409));
    }

    @Test
    void noIntent_throwsBadRequest() {
        when(hocKyRepository.findById(10L)).thenReturn(Optional.of(hk));
        when(hocPhanRepository.findById(99L)).thenReturn(Optional.of(hp));
        when(intentRepository.aggregateDemand(eq(10L), eq(2021), eq(3L))).thenReturn(Collections.emptyList());

        PreRegistrationPlanSectionsRequest req = PreRegistrationPlanSectionsRequest.builder()
                .hocKyId(10L)
                .idHocPhan(99L)
                .namNhapHoc(2021)
                .idNganh(3L)
                .build();

        assertThatThrownBy(() -> service.planSections(req, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(400));
    }
}
