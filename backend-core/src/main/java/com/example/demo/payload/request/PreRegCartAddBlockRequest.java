package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreRegCartAddBlockRequest {
    @NotNull
    private Long idTkbBlock;
    private Long hocKyId;
}
