package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingConflictItemResponse {
    /** VD: PHONG_TRUNG, GV_TRUNG */
    private String conflictType;
    private Long conflictingLopHpId;
    /** Một dòng đọc được cho admin (ghi chú ngắn) */
    private String detail;
}
