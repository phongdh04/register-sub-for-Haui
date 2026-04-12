package com.example.demo.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * DTO nhận message từ Kafka topic "eduport.dang-ky-hoc-phan".
 * Fields khớp chính xác với struct DangKyMessage trong Go service (snake_case JSON).
 *
 * SRP: Chỉ chứa dữ liệu raw từ Kafka, không chứa business logic.
 * DIP: Consumer không cần biết Go Service, chỉ cần biết contract JSON này.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationMessageDto {

    @JsonProperty("id_sinh_vien")
    private Long idSinhVien;

    @JsonProperty("id_lop_hp")
    private Long idLopHp;

    @JsonProperty("id_hoc_ky")
    private Long idHocKy;

    @JsonProperty("thoi_gian_gui")
    private Instant thoiGianGui;

    /**
     * TraceID để theo dõi luồng end-to-end từ Go → Kafka → Java → DB.
     * Format: "DKHP-{svId}-{lopHpId}-{timestamp}" hoặc "CANCEL-..." khi hủy.
     */
    @JsonProperty("trace_id")
    private String traceId;

    /**
     * Kiểm tra đây có phải là message hủy đăng ký không.
     * TraceID của message hủy bắt đầu bằng "CANCEL-".
     */
    public boolean isCancellation() {
        return traceId != null && traceId.startsWith("CANCEL-");
    }
}
