package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScheduleChangeSetApplyRequest {

    private String appliedBy;

    @NotBlank
    private String lyDoThayDoi;
}
