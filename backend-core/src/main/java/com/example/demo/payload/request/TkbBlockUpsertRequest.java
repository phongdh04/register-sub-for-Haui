package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TkbBlockUpsertRequest {

    @NotBlank
    private String maBlock;

    @NotBlank
    private String tenBlock;

    private List<Map<String, Object>> jsonSlots;

    private List<Long> danhSachIdHocPhan;

    private Boolean batBuocChonCaBlock;
}
