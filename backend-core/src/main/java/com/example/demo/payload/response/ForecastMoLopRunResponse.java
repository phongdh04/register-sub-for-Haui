package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastMoLopRunResponse {
    private Long idDuBaoVersion;
    private Long hocKyId;
    private Long idChuongTrinhDaoTao;
    private String trangThai;
    private Instant createdAt;
    private Integer siSoMacDinhUsed;
    private BigDecimal heSoDuPhongUsed;
    private BigDecimal tyLeSvHocLaiUsed;
    private Short namHocNamKeStored;
    private Integer kyThuMucTieuStored;

    /** Tổng số học phần có trong phiên (sau filter bat_buoc + hoc_ky_goi_y). */
    private int lineCount;
    private Long tongSoLopDeXuat;
    private Long tongSoSvDuKien;

    private List<ForecastMoLopLineItemResponse> lines;
}
