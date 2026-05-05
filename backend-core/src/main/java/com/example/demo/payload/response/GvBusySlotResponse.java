package com.example.demo.payload.response;

import com.example.demo.domain.enums.GvBusyLoai;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GvBusySlotResponse {

    private Long id;
    private Long hocKyId;
    private Short thu;
    private Short tietBd;
    private Short tietKt;
    private GvBusyLoai loai;
    private String lyDo;
    private LocalDate ngayBd;
    private LocalDate ngayKt;
    private Instant createdAt;
}
