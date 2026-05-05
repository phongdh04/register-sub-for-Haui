package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiangVienConstraintsResponse {

    private Long giangVienId;
    private List<GvBusySlotResponse> busySlots;

    /** Dự phòng (TKB P2+): định mức giờ học kỳ, cờ thỉnh giảng, JSON preference. */
    private Object extended;
}
