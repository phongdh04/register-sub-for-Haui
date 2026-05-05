package com.example.demo.domain.entity;

import com.example.demo.domain.enums.LoaiPhong;
import com.example.demo.domain.enums.TrangThaiPhong;
import jakarta.persistence.*;
import lombok.*;

/**
 * Master phòng học (scheduling, capacity, loại phòng).
 * Bảng: {@code Phong_Hoc}
 */
@Entity
@Table(
        name = "Phong_Hoc",
        indexes = {
                @Index(name = "idx_phong_ma_co_so", columnList = "ma_co_so"),
                @Index(name = "idx_phong_loai", columnList = "loai_phong")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phong")
    private Long idPhong;

    @Column(name = "ma_phong", unique = true, nullable = false, length = 30)
    private String maPhong;

    @Column(name = "ten_phong", nullable = false, length = 200)
    private String tenPhong;

    /** Mã cơ sở vật lý (ví dụ CS1, CS2, ONLINE) — đơn giản hóa FK CoSo nếu chưa có bảng master. */
    @Column(name = "ma_co_so", nullable = false, length = 50)
    private String maCoSo;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phong", nullable = false, length = 40)
    private LoaiPhong loaiPhong;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiPhong trangThai = TrangThaiPhong.HOAT_DONG;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
}
