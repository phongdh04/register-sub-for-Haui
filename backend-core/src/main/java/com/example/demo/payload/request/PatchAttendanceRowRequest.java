package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatchAttendanceRowRequest {
    /** CO_MAT | VANG | PHEP */
    @NotBlank
    private String trangThai;
}
