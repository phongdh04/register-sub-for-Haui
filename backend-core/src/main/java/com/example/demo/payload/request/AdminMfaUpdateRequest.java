package com.example.demo.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminMfaUpdateRequest {
    @NotNull
    private Boolean enabled;

    /** Bắt buộc khi bật MFA. */
    @Email(message = "Email không hợp lệ")
    private String email;
}
