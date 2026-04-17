package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MfaVerifyRequest {
    @NotBlank
    private String challengeId;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$", message = "OTP phải gồm 6 chữ số")
    private String otp;
}
