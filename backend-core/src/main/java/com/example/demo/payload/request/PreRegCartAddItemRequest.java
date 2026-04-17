package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreRegCartAddItemRequest {
    @NotNull
    private Long idLopHp;
    /** Nếu null: dùng học kỳ hiện hành hoặc học kỳ mới nhất. */
    private Long hocKyId;
}
