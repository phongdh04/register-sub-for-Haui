package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleChangeSetReviewRequest {

    @NotNull
    private Boolean approve;

    private String reviewedBy;

    @NotBlank
    private String lyDoThayDoi;

    private String reviewNote;
}
