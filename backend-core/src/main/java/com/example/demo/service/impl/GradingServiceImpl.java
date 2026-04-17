package com.example.demo.service.impl;

import com.example.demo.domain.entity.BangDiemMon;
import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.LecturerUpdateGradeRequest;
import com.example.demo.payload.response.GradebookRowResponse;
import com.example.demo.payload.response.LecturerGradebookResponse;
import com.example.demo.repository.BangDiemMonRepository;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IAuditTrailService;
import com.example.demo.service.IGradingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements IGradingService {

    private final UserRepository userRepository;
    private final GiangVienRepository giangVienRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final BangDiemMonRepository bangDiemMonRepository;
    private final IAuditTrailService auditTrailService;

    @Override
    @Transactional(readOnly = true)
    public LecturerGradebookResponse getGradebook(String username, Long idLopHp) {
        GiangVien gv = resolveGiangVien(username);
        LopHocPhan lop = lopHocPhanRepository.findWithGiangVienForAttendance(idLopHp)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần: " + idLopHp));
        assertLecturerOwnsLop(lop, gv);

        List<DangKyHocPhan> rows = dangKyHocPhanRepository.findGradebookRowsForLop(idLopHp);
        var hk = lop.getHocKy();
        String hkLabel = hk.getNamHoc() + " — Kỳ " + hk.getKyThu();

        return LecturerGradebookResponse.builder()
                .idLopHp(lop.getIdLopHp())
                .maLopHp(lop.getMaLopHp())
                .tenHocPhan(lop.getHocPhan().getTenHocPhan())
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(hkLabel)
                .siSo(rows.size())
                .rows(rows.stream().map(this::toGradeRow).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public GradebookRowResponse saveDraftGrade(String username, Long idDangKy, LecturerUpdateGradeRequest request) {
        GiangVien gv = resolveGiangVien(username);
        DangKyHocPhan dk = dangKyHocPhanRepository.findWithLopAndGiangVienForGrade(idDangKy)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đăng ký: " + idDangKy));
        assertLecturerOwnsLop(dk.getLopHocPhan(), gv);

        BigDecimal diem = request.getDiemHe4().setScale(2, RoundingMode.HALF_UP);
        if (diem.compareTo(BigDecimal.ZERO) < 0 || diem.compareTo(new BigDecimal("4.0")) > 0) {
            throw new IllegalArgumentException("Điểm hệ 4 phải nằm trong [0, 4].");
        }

        BangDiemMon bdm = bangDiemMonRepository.findByDangKyHocPhan_IdDangKy(idDangKy).orElse(null);
        if (isLockedForDraftEdit(bdm)) {
            throw new IllegalArgumentException("Điểm đã công bố — không thể sửa nháp.");
        }
        if (bdm == null) {
            bdm = BangDiemMon.builder()
                    .dangKyHocPhan(dk)
                    .trangThai("CHO_CONG_BO")
                    .build();
        }

        bdm.setDiemHe4(diem);
        bdm.setDiemChu(letterGrade4(diem));
        if (bdm.getTrangThai() == null) {
            bdm.setTrangThai("CHO_CONG_BO");
        }
        bdm = bangDiemMonRepository.save(bdm);
        dk.setBangDiemMon(bdm);
        auditTrailService.record(username, "LECTURER", "GRADING_DRAFT_SAVE",
                "Lưu nháp điểm đăng ký " + idDangKy,
                Map.of(
                        "idDangKy", idDangKy,
                        "idLopHp", dk.getLopHocPhan().getIdLopHp(),
                        "maLopHp", dk.getLopHocPhan().getMaLopHp(),
                        "diemHe4", diem));
        return toGradeRow(dk);
    }

    @Override
    @Transactional
    public GradebookRowResponse publishGrade(String username, Long idDangKy) {
        GiangVien gv = resolveGiangVien(username);
        DangKyHocPhan dk = dangKyHocPhanRepository.findWithLopAndGiangVienForGrade(idDangKy)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đăng ký: " + idDangKy));
        assertLecturerOwnsLop(dk.getLopHocPhan(), gv);

        BangDiemMon bdm = bangDiemMonRepository.findByDangKyHocPhan_IdDangKy(idDangKy)
                .orElseThrow(() -> new IllegalArgumentException("Chưa có bản ghi điểm."));
        if (bdm.getDiemHe4() == null) {
            throw new IllegalArgumentException("Chưa có điểm để công bố.");
        }
        bdm.setTrangThai("DA_CONG_BO");
        bangDiemMonRepository.save(bdm);
        dk.setBangDiemMon(bdm);
        auditTrailService.record(username, "LECTURER", "GRADING_PUBLISH",
                "Công bố điểm đăng ký " + idDangKy,
                Map.of(
                        "idDangKy", idDangKy,
                        "idLopHp", dk.getLopHocPhan().getIdLopHp(),
                        "maLopHp", dk.getLopHocPhan().getMaLopHp(),
                        "diemHe4", bdm.getDiemHe4()));
        return toGradeRow(dk);
    }

    private boolean isLockedForDraftEdit(BangDiemMon bdm) {
        if (bdm == null) {
            return false;
        }
        if ("DA_CONG_BO".equals(bdm.getTrangThai())) {
            return true;
        }
        return bdm.getTrangThai() == null && bdm.getDiemHe4() != null;
    }

    private GiangVien resolveGiangVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        return giangVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa được liên kết với hồ sơ giảng viên."));
    }

    private void assertLecturerOwnsLop(LopHocPhan lop, GiangVien gv) {
        if (lop.getGiangVien() == null || !Objects.equals(lop.getGiangVien().getIdGiangVien(), gv.getIdGiangVien())) {
            throw new AccessDeniedException("Bạn không phải giảng viên phụ trách lớp này.");
        }
    }

    private GradebookRowResponse toGradeRow(DangKyHocPhan d) {
        var sv = d.getSinhVien();
        BangDiemMon b = d.getBangDiemMon();
        return GradebookRowResponse.builder()
                .idDangKy(d.getIdDangKy())
                .maSinhVien(sv.getMaSinhVien())
                .hoTen(sv.getHoTen())
                .idBangDiem(b != null ? b.getIdBangDiem() : null)
                .diemHe4(b != null ? b.getDiemHe4() : null)
                .diemChu(b != null ? b.getDiemChu() : null)
                .trangThaiBangDiem(b != null ? b.getTrangThai() : null)
                .build();
    }

    /** Thang chữ đơn giản trên thang 4.0 (minh họa đồ án). */
    private String letterGrade4(BigDecimal d) {
        if (d.compareTo(new BigDecimal("3.65")) >= 0) {
            return "A";
        }
        if (d.compareTo(new BigDecimal("3.35")) >= 0) {
            return "B+";
        }
        if (d.compareTo(new BigDecimal("3.0")) >= 0) {
            return "B";
        }
        if (d.compareTo(new BigDecimal("2.65")) >= 0) {
            return "C+";
        }
        if (d.compareTo(new BigDecimal("2.0")) >= 0) {
            return "C";
        }
        if (d.compareTo(new BigDecimal("1.65")) >= 0) {
            return "D+";
        }
        if (d.compareTo(new BigDecimal("1.0")) >= 0) {
            return "D";
        }
        return "F";
    }
}
