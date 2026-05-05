package com.example.demo.service;

import com.example.demo.payload.request.GvBusySlotUpsertRequest;
import com.example.demo.payload.response.GiangVienConstraintsResponse;
import com.example.demo.payload.response.GvBusySlotResponse;

public interface IGiangVienConstraintsService {

    GiangVienConstraintsResponse getConstraints(Long giangVienId, Long hocKyFilter);

    GvBusySlotResponse createBusySlot(Long giangVienId, GvBusySlotUpsertRequest request);

    GvBusySlotResponse updateBusySlot(Long giangVienId, Long slotId, GvBusySlotUpsertRequest request);

    void deleteBusySlot(Long giangVienId, Long slotId);
}
