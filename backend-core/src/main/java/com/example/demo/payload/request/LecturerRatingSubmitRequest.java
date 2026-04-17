package com.example.demo.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LecturerRatingSubmitRequest {

    @NotNull
    private Long idDangKy;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer diemTong;

    @Size(max = 2000)
    private String binhLuan;
}
