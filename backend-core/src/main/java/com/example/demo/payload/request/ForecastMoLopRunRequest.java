package com.example.demo.payload.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ForecastMoLopRunRequest {

    /** CTĐT dùng để liệt kê học phần + khớp cohort ngành. */
    @NotNull
    private Long idChuongTrinhDaoTao;

    @Min(1)
    private Integer siSoToiDaMacDinh;

    @DecimalMin(value = "1.0", inclusive = false)
    private BigDecimal heSoDuPhong;

    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal tyLeSvHocLaiThamGia;
}
