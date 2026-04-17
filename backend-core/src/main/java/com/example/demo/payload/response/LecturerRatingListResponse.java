package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LecturerRatingListResponse {
    private Long idHocKy;
    private String hocKyLabel;
    private List<LecturerRatingRowResponse> rows;
}
