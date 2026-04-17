package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaLoginChallengeResponse {
    private boolean requiresMfa;
    private String challengeId;
    private String emailMasked;
    private String username;
    /** Gợi ý cho demo khi chưa có SMTP. */
    private String hint;
}
