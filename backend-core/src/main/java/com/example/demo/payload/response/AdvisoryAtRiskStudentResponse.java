package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Task 19 – một sinh viên cảnh báo học vụ (tín chỉ rớt tích lũy cao).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisoryAtRiskStudentResponse {

    private Long idSinhVien;
    private String maSinhVien;
    private String hoTen;
    private String maLop;
    private String tenLop;
    /** Tổng tín chỉ các môn đã công bố với điểm hệ 4 dưới 1.0 */
    private int tinChiRot;
    private long soMonRot;
    /** GPA tích lũy (điểm đã công bố); null nếu chưa đủ dữ liệu */
    private BigDecimal gpaTichLuy;
}
