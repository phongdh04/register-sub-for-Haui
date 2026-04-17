package com.example.demo.payload.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAttendanceSessionRequest {
    /** Nếu null: dùng ngày hiện tại theo hệ thống. */
    private LocalDate ngayBuoi;
}
