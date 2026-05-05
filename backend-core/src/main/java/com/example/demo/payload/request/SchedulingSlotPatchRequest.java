package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SchedulingSlotPatchRequest {

    @NotNull
    private List<Map<String, Object>> slots;

    private Long overrideGiangVienId;

    private Long overridePhongHocId;

    @NotBlank
    private String lyDoThayDoi;
}
