package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRetakeAppealRequest {

    @NotNull
    private Long idDangKy;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String lyDoSinhVien;
}
