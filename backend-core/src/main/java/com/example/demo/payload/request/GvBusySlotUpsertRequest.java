package com.example.demo.payload.request;

import com.example.demo.domain.enums.GvBusyLoai;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GvBusySlotUpsertRequest {

    /** FK học kỳ; để null = busy áp dụng mọi HK. */
    private Long hocKyId;

    @NotNull
    @Min(2)
    @Max(8)
    private Short thu;

    @NotNull
    private Short tietBd;

    @NotNull
    private Short tietKt;

    private GvBusyLoai loai;

    @Size(max = 500)
    private String lyDo;

    private LocalDate ngayBd;
    private LocalDate ngayKt;
}
