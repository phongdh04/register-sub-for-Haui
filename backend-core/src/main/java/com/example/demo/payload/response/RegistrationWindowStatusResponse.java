package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Trang thai cua so dang ky cho 1 sinh vien (cohort + nganh).
 *
 * <p>Dung de UI hien banner "Dang ky dang mo cho ban — con N gio" hoac
 * "Chua mo cho khoa/nganh cua ban".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationWindowStatusResponse {

    private Long idHocKy;
    private String tenHocKy;

    private String maSinhVien;
    private String hoTenSinhVien;
    private String tenLop;
    private Integer namNhapHoc;
    private Long idNganh;
    private String tenNganh;

    private boolean dangKyChinhThucDangMo;
    private Instant officialOpenAt;
    private Instant officialCloseAt;
    private String officialScopeNote;

    private boolean preDangKyDangMo;
    private Instant preOpenAt;
    private Instant preCloseAt;
    private String preScopeNote;

    /** Tien loi cho frontend: PRE hoac OFFICIAL dang mo. */
    private boolean dangKyAnyDangMo;

    /** Phase dang mo: NONE | PRE | OFFICIAL | PRE_AND_OFFICIAL (dong thoi PRE+OFFICIAL). */
    private String activePhase;

    /** Goi y ly do neu dong (debug cho admin/dev). */
    private String debugReason;
}
