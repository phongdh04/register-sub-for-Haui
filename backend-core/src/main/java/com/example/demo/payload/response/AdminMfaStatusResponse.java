package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMfaStatusResponse {
    private boolean mfaEnabled;
    private String emailMasked;
    private boolean hasEmail;
}
