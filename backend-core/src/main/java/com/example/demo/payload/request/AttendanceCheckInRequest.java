package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendanceCheckInRequest {
    @NotBlank
    private String token;
}
