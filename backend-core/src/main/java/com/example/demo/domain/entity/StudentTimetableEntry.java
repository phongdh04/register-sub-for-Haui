package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Sprint 5 — Read-model TKB của sinh viên (1 hàng = 1 slot trong tuần của 1 đăng ký).
 *
 * <p>Read-model này được cập nhật bởi {@code RegistrationTimetableProjectionListener}
 * khi nhận event {@code RegistrationConfirmedEvent} / {@code RegistrationCancelledEvent}
 * (phase {@code AFTER_COMMIT}).
 *
 * <p>Mục đích:
 * <ul>
 *   <li>Render TKB sinh viên không cần parse JSON {@code thoi_khoa_bieu_json} mỗi lần.</li>
 *   <li>Phản ánh ngay sau khi đăng ký commit (tránh delay UX).</li>
 *   <li>Phục vụ downstream: export iCal, push notification "lớp X trong tuần này", dashboard admin.</li>
 * </ul>
 *
 * <p>UNIQUE {@code (id_dang_ky, slot_index)} → idempotent: re-emit event không tạo bản ghi trùng.
 */
@Entity
@Table(
        name = "student_timetable_entry",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_stt_dangky_slot",
                        columnNames = {"id_dang_ky", "slot_index"})
        },
        indexes = {
                @Index(name = "idx_stt_sv_hk", columnList = "id_sinh_vien, id_hoc_ky"),
                @Index(name = "idx_stt_lhp", columnList = "id_lop_hp"),
                @Index(name = "idx_stt_dangky", columnList = "id_dang_ky")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entry")
    private Long id;

    @Column(name = "id_sinh_vien", nullable = false)
    private Long idSinhVien;

    @Column(name = "id_hoc_ky", nullable = false)
    private Long idHocKy;

    @Column(name = "id_dang_ky", nullable = false)
    private Long idDangKy;

    @Column(name = "id_lop_hp", nullable = false)
    private Long idLopHp;

    @Column(name = "ma_lop_hp", length = 30)
    private String maLopHp;

    @Column(name = "ma_hoc_phan", length = 30)
    private String maHocPhan;

    @Column(name = "ten_hoc_phan", length = 255)
    private String tenHocPhan;

    @Column(name = "ten_giang_vien", length = 255)
    private String tenGiangVien;

    @Column(name = "slot_index", nullable = false)
    private Short slotIndex;

    @Column(name = "thu")
    private Short thu;

    @Column(name = "tiet", length = 20)
    private String tiet;

    @Column(name = "phong", length = 50)
    private String phong;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
    }
}
