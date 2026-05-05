package com.example.demo.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** §6.3 — dự báo sĩ số và số lớp đề xuất. */
public final class DuBaoMoLopCalculator {

    private DuBaoMoLopCalculator() {
    }

    /** {@code demand = N_on + ceil(N_retake * ty_le)} */
    public static int demandSv(int nOnTrack, int nRetake, BigDecimal tyLeSvHocLaiThamGia) {
        double weighted = Math.ceil(tyLeSvHocLaiThamGia.doubleValue() * nRetake);
        return nOnTrack + (int) weighted;
    }

    /** {@code sections = ceil(demand * he_so_du_phong / si_so_toi_da_mac_dinh)} */
    public static int soLopDeXuat(int demandSv, BigDecimal heSoDuPhong, int siSoToiDaMacDinh) {
        if (siSoToiDaMacDinh <= 0) {
            throw new IllegalArgumentException("siSoToiDaMacDinh phải > 0");
        }
        BigDecimal d = BigDecimal.valueOf(demandSv);
        BigDecimal x = d.multiply(heSoDuPhong);
        BigDecimal q = x.divide(BigDecimal.valueOf(siSoToiDaMacDinh), 0, RoundingMode.CEILING);
        return q.intValue();
    }
}
