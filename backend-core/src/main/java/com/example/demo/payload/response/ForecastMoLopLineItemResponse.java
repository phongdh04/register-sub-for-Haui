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
public class ForecastMoLopLineItemResponse {
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer hocKyGoiYCtdt;
    private Integer soSvOnTrack;
    private Integer soSvHocLai;
    private Integer soSvDuKien;
    private Integer soLopDeXuat;
}
