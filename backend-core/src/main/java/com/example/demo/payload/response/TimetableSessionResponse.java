package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimetableSessionResponse {
    private Integer thu;
    private String tiet;
    private String phong;
    private String ngayBatDau;
    private String ngayKetThuc;
}
