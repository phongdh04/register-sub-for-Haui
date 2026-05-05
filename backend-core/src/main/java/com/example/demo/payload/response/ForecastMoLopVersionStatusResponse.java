package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** BACK-TKB-016 — trạng thái phiên dự báo sau approve/reject. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastMoLopVersionStatusResponse {
    private Long idDuBaoVersion;
    private Long hocKyId;
    private Long idChuongTrinhDaoTao;
    private String trangThai;
    private Instant createdAt;
}
