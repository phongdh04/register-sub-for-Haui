package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LopHocPhanAssignGiangVienRequest {

    @NotNull
    private Long idGiangVien;
}
