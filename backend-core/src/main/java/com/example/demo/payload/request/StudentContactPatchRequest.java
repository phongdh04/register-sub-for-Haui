package com.example.demo.payload.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentContactPatchRequest {

    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String sdt;

    @Size(max = 500)
    private String diaChi;
}
