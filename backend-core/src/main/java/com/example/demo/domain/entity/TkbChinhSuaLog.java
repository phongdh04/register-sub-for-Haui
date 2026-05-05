package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/** BACK-TKB-033 — log chỉnh sửa TKB theo change-set workflow. */
@Entity
@Table(
        name = "tkb_chinh_sua_log",
        indexes = {
                @Index(name = "idx_tkb_chinh_sua_log_hk", columnList = "hoc_ky_id"),
                @Index(name = "idx_tkb_chinh_sua_log_cs", columnList = "change_set_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TkbChinhSuaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hoc_ky_id", nullable = false)
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_set_id")
    private ScheduleChangeSet changeSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lop_hp")
    private LopHocPhan lopHocPhan;

    @Column(name = "hanh_dong", nullable = false, length = 30)
    private String hanhDong;

    @Column(name = "nguoi_thuc_hien", length = 120)
    private String nguoiThucHien;

    @Column(name = "ly_do_thay_doi", nullable = false, length = 1000)
    private String lyDoThayDoi;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_cu", columnDefinition = "jsonb")
    private Map<String, Object> payloadCu;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_moi", columnDefinition = "jsonb")
    private Map<String, Object> payloadMoi;

    @Column(name = "effective_version_no")
    private Long effectiveVersionNo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
