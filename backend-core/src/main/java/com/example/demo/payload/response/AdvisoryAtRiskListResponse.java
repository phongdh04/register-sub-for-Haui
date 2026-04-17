package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisoryAtRiskListResponse {

    private String khoaMa;
    private String khoaTen;
    private int minFailedCredits;
    private int tongSoSinhVien;
    /** Gợi ý khi GV chưa gán khoa hoặc không có bản ghi */
    private String hint;
    private List<AdvisoryAtRiskStudentResponse> sinhViens;
}
