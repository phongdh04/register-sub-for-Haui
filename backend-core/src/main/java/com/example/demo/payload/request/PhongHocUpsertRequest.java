package com.example.demo.payload.request;

import com.example.demo.domain.enums.LoaiPhong;
import com.example.demo.domain.enums.TrangThaiPhong;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongHocUpsertRequest {

    @NotBlank
    @Size(max = 30)
    private String maPhong;

    @NotBlank
    @Size(max = 200)
    private String tenPhong;

    @NotBlank
    @Size(max = 50)
    private String maCoSo;

    @NotNull
    private LoaiPhong loaiPhong;

    @NotNull
    @Positive
    private Integer sucChua;

    private TrangThaiPhong trangThai;

    private String ghiChu;
}
