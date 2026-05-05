package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

/**
 * Gói TKB trong học kỳ — đăng ký bundle các LHP cùng block (§7.6).
 * P0: chỉ entity + DDL; lifecycle CRUD chờ BACK-TKB-037 (P4).
 */
@Entity
@Table(
        name = "Tkb_Block",
        indexes = {
                @Index(name = "idx_tkb_block_hk", columnList = "id_hoc_ky")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tkb_block_hk_ma", columnNames = { "id_hoc_ky", "ma_block" })
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TkbBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tkb_block")
    private Long idTkbBlock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @Column(name = "ma_block", nullable = false, length = 64)
    private String maBlock;

    @Column(name = "ten_block", nullable = false, length = 300)
    private String tenBlock;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_slots", columnDefinition = "jsonb")
    private List<Map<String, Object>> jsonSlots;

    /** Danh sách {@code id_hoc_phan} thuộc gói (JSON array số nguyên). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "danh_sach_id_hoc_phan_json", columnDefinition = "jsonb")
    private List<Long> danhSachIdHocPhanJson;

    @Column(name = "bat_buoc_chon_ca_block", nullable = false)
    @Builder.Default
    private Boolean batBuocChonCaBlock = false;
}
