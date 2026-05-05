package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.domain.enums.ScheduleChangeSetStatus;
import com.example.demo.payload.response.ScheduleImpactAnalysisResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.ScheduleChangeSetRepository;
import com.example.demo.service.INotificationQueueService;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.repository.TkbChinhSuaLogRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleChangeSetServiceImplTest {

    @Mock
    private HocKyRepository hocKyRepository;
    @Mock
    private ScheduleChangeSetRepository scheduleChangeSetRepository;
    @Mock
    private DangKyHocPhanRepository dangKyHocPhanRepository;
    @Mock
    private LopHocPhanRepository lopHocPhanRepository;
    @Mock
    private TkbChinhSuaLogRepository tkbChinhSuaLogRepository;
    @Mock
    private INotificationQueueService notificationQueueService;
    @Mock
    private ITkbRevisionService tkbRevisionService;

    @InjectMocks
    private ScheduleChangeSetServiceImpl service;

    @Test
    void analyzeImpact_extractsTargetLhpAndCountsDistinctStudents() {
        Map<String, Object> delta = new LinkedHashMap<>();
        delta.put("targetLopHpIds", java.util.List.of(100L, "101"));

        when(dangKyHocPhanRepository.findDistinctSuccessSinhVienIdsByHocKyAndLopHpIds(8L, Set.of(100L, 101L)))
                .thenReturn(new java.util.LinkedHashSet<>(java.util.List.of(5L, 2L, 5L)));

        ScheduleImpactAnalysisResponse out = service.analyzeImpact(8L, delta);

        Assertions.assertEquals(2, out.affectedSvCount());
        Assertions.assertEquals(java.util.List.of(2L, 5L), out.affectedSvIds());
        Assertions.assertEquals(java.util.List.of(100L, 101L), out.targetLopHpIds());
    }

    @Test
    void createPending_enqueuesScheduleChangedNotification() {
        HocKy hk = HocKy.builder().idHocKy(3L).build();
        when(hocKyRepository.findById(3L)).thenReturn(Optional.of(hk));
        when(dangKyHocPhanRepository.findDistinctSuccessSinhVienIdsByHocKyAndLopHpIds(3L, Set.of(44L)))
                .thenReturn(Set.of(11L, 12L));
        when(scheduleChangeSetRepository.save(any(ScheduleChangeSet.class)))
                .thenAnswer(inv -> {
                    ScheduleChangeSet cs = inv.getArgument(0);
                    cs.setId(99L);
                    return cs;
                });

        Map<String, Object> delta = Map.of("idLopHp", 44L);
        ScheduleChangeSet saved = service.createPending(3L, delta, "admin", "note");

        Assertions.assertEquals(2, saved.getAffectedSvCount());
        Assertions.assertEquals(java.util.List.of(11L, 12L), saved.getAffectedSvIds());
        verify(notificationQueueService).enqueueScheduleChanged(saved, java.util.List.of(11L, 12L));
    }

    @Test
    void apply_approvedChangeSet_bumpsRevisionAndAppliedStatus() {
        HocKy hk = HocKy.builder().idHocKy(3L).tkbRevision(7L).build();
        ScheduleChangeSet cs = ScheduleChangeSet.builder()
                .id(99L)
                .hocKy(hk)
                .trangThai(ScheduleChangeSetStatus.APPROVED)
                .payloadDelta(Map.of("changes", java.util.List.of(
                        Map.of("idLopHp", 44L, "slots", java.util.List.of(Map.of("thu", 2, "tiet", "1-1"))))))
                .build();
        hk.setPendingChangeSet(cs);
        when(scheduleChangeSetRepository.findById(99L)).thenReturn(Optional.of(cs));
        when(scheduleChangeSetRepository.save(any(ScheduleChangeSet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lopHocPhanRepository.findById(44L)).thenReturn(Optional.of(LopHocPhan.builder().idLopHp(44L).build()));

        ScheduleChangeSet out = service.apply(3L, 99L, "admin", "duyet va ap dung");

        Assertions.assertEquals(ScheduleChangeSetStatus.APPLIED, out.getTrangThai());
        Assertions.assertEquals(8L, out.getEffectiveVersionNo());
        verify(tkbRevisionService).bumpAfterTkbMutation(3L);
    }
}
