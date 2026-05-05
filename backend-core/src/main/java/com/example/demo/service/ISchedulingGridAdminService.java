package com.example.demo.service;

import com.example.demo.payload.request.SchedulingConflictCheckRequest;
import com.example.demo.payload.request.SchedulingSlotPatchRequest;
import com.example.demo.payload.response.SchedulingConflictCheckResponse;
import com.example.demo.payload.response.SchedulingSlotPatchResponse;
import com.example.demo.payload.response.SchedulingSnapshotResponse;

/** BK-TKB-011 / 012 — snapshot warm cache và conflict-check theo học kỳ (admin scheduling). */
public interface ISchedulingGridAdminService {

    SchedulingSnapshotResponse getSnapshot(Long hocKyId);

    SchedulingConflictCheckResponse conflictCheck(Long hocKyId, SchedulingConflictCheckRequest request);

    SchedulingSlotPatchResponse patchSlot(Long idLopHp, SchedulingSlotPatchRequest request);
}
