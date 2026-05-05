package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingConflictCheckRequest {

    @NotNull
    private Long idLopHp;

    /** Bản nháp TKB (map slot như {@code thoi_khoa_bieu_json}); rỗng → không chồng tiết (chỉ dùng khi chỉnh sửa từng phần) */
    @NotNull
    private List<Map<String, Object>> slots;

    /** Áp vào các slot không có FK GV trong map */
    private Long overrideGiangVienId;
    /** Áp vào các slot không có FK phòng trong map */
    private Long overridePhongHocId;
}
