package com.example.demo.service;

import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.payload.response.ScheduleImpactAnalysisResponse;

import java.util.List;
import java.util.Map;

/** BACK-TKB-029 — thao tác cơ bản cho schedule_change_set (P3 groundwork). */
public interface IScheduleChangeSetService {

    ScheduleChangeSet createPending(Long hocKyId, Map<String, Object> payloadDelta, String requestedBy, String note);

    List<ScheduleChangeSet> listByHocKy(Long hocKyId);

    ScheduleImpactAnalysisResponse analyzeImpact(Long hocKyId, Map<String, Object> payloadDelta);

    ScheduleChangeSet review(Long hocKyId, Long changeSetId, boolean approve, String reviewedBy, String lyDoThayDoi, String note);

    ScheduleChangeSet apply(Long hocKyId, Long changeSetId, String appliedBy, String lyDoThayDoi);
}
