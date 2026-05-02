package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GioHangDangKy;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.PreRegCartAddItemRequest;
import com.example.demo.payload.response.PreRegCartItemResponse;
import com.example.demo.payload.response.PreRegCartResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GioHangDangKyRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IPreRegistrationCartService;
import com.example.demo.support.RegistrationScheduleChecker;
import com.example.demo.util.TkbSlotConflictUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreRegistrationCartServiceImpl implements IPreRegistrationCartService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final GioHangDangKyRepository gioHangDangKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final RegistrationScheduleChecker registrationScheduleChecker;

    @Override
    @Transactional(readOnly = true)
    public PreRegCartResponse getMyCart(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        HocKy hk = resolveHocKy(hocKyId);
        return buildCartResponse(sv, hk);
    }

    @Override
    @Transactional
    public PreRegCartItemResponse addItem(String username, PreRegCartAddItemRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        HocKy hk = resolveHocKy(request.getHocKyId());
        registrationScheduleChecker.requirePreRegistrationOpen(hk);

        LopHocPhan lhp = lopHocPhanRepository.findById(request.getIdLopHp())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần: " + request.getIdLopHp()));

        if (!lhp.getHocKy().getIdHocKy().equals(hk.getIdHocKy())) {
            throw new IllegalArgumentException("Lớp học phần không thuộc học kỳ đang chọn.");
        }

        if (gioHangDangKyRepository.existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(
                sv.getIdSinhVien(), lhp.getIdLopHp(), hk.getIdHocKy())) {
            throw new IllegalArgumentException("Lớp đã có trong giỏ đăng ký trước.");
        }

        if (dangKyHocPhanRepository.existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(
                sv.getIdSinhVien(), lhp.getIdLopHp(), hk.getIdHocKy())) {
            throw new IllegalArgumentException("Bạn đã đăng ký chính thức lớp này trong học kỳ này.");
        }

        GioHangDangKy row = GioHangDangKy.builder()
                .sinhVien(sv)
                .lopHocPhan(lhp)
                .hocKy(hk)
                .build();
        row = gioHangDangKyRepository.save(row);
        return toItemResponse(row);
    }

    @Override
    @Transactional
    public void removeItem(String username, Long idGioHang) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        GioHangDangKy g = gioHangDangKyRepository.findByIdGioHangAndSinhVien_IdSinhVien(idGioHang, sv.getIdSinhVien())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dòng giỏ hàng."));
        gioHangDangKyRepository.delete(g);
    }

    private HocKy resolveHocKy(Long hocKyId) {
        if (hocKyId != null) {
            return hocKyRepository.findById(hocKyId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        }
        return hocKyRepository.findByTrangThaiHienHanhTrue()
                .orElseGet(() -> hocKyRepository.findTopByOrderByIdHocKyDesc()
                        .orElseThrow(() -> new EntityNotFoundException("Hệ thống chưa cấu hình học kỳ.")));
    }

    private PreRegCartResponse buildCartResponse(SinhVien sv, HocKy hk) {
        List<GioHangDangKy> rows = gioHangDangKyRepository.findBySinhVienAndHocKyWithLop(sv.getIdSinhVien(), hk.getIdHocKy());

        int tongTc = 0;
        BigDecimal tongPhi = BigDecimal.ZERO;
        List<LopHocPhan> lops = new ArrayList<>();
        for (GioHangDangKy g : rows) {
            LopHocPhan l = g.getLopHocPhan();
            lops.add(l);
            HocPhan hp = l.getHocPhan();
            if (hp != null && hp.getSoTinChi() != null) {
                tongTc += hp.getSoTinChi();
            }
            if (l.getHocPhi() != null) {
                tongPhi = tongPhi.add(l.getHocPhi());
            }
        }

        int internalPairs = countInternalConflictPairs(lops);
        boolean vsReg = conflictsWithOfficialRegistrations(sv.getIdSinhVien(), hk.getIdHocKy(), lops);

        return PreRegCartResponse.builder()
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(formatHocKy(hk))
                .tongSoMon(rows.size())
                .tongTinChi(tongTc)
                .tongHocPhi(tongPhi)
                .soDoiTrungLichTrongGioHang(internalPairs)
                .coTrungLichVoiDangKyChinhThuc(vsReg)
                .items(rows.stream().map(this::toItemResponse).collect(Collectors.toList()))
                .preDangKyMoTu(hk.getPreDangKyMoTu())
                .preDangKyMoDen(hk.getPreDangKyMoDen())
                .dangKyChinhThucTu(hk.getDangKyChinhThucTu())
                .dangKyChinhThucDen(hk.getDangKyChinhThucDen())
                .preDangKyDangMo(registrationScheduleChecker.isPreRegistrationOpen(hk))
                .dangKyChinhThucDangMo(registrationScheduleChecker.isOfficialRegistrationOpen(hk))
                .build();
    }

    private int countInternalConflictPairs(List<LopHocPhan> lops) {
        int c = 0;
        for (int i = 0; i < lops.size(); i++) {
            for (int j = i + 1; j < lops.size(); j++) {
                if (TkbSlotConflictUtils.listsConflict(
                        lops.get(i).getThoiKhoaBieuJson(),
                        lops.get(j).getThoiKhoaBieuJson())) {
                    c++;
                }
            }
        }
        return c;
    }

    private boolean conflictsWithOfficialRegistrations(Long idSinhVien, Long idHocKy, List<LopHocPhan> cartLops) {
        List<DangKyHocPhan> regs = dangKyHocPhanRepository.findRegisteredCoursesInSemester(idSinhVien, idHocKy);
        for (LopHocPhan ca : cartLops) {
            for (DangKyHocPhan d : regs) {
                LopHocPhan cb = d.getLopHocPhan();
                if (TkbSlotConflictUtils.listsConflict(ca.getThoiKhoaBieuJson(), cb.getThoiKhoaBieuJson())) {
                    return true;
                }
            }
        }
        return false;
    }

    private PreRegCartItemResponse toItemResponse(GioHangDangKy g) {
        LopHocPhan l = g.getLopHocPhan();
        HocPhan hp = l.getHocPhan();
        HocKy hk = g.getHocKy();
        return PreRegCartItemResponse.builder()
                .idGioHang(g.getIdGioHang())
                .idLopHp(l.getIdLopHp())
                .maLopHp(l.getMaLopHp())
                .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .soTinChi(hp != null ? hp.getSoTinChi() : null)
                .hocPhi(l.getHocPhi())
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(formatHocKy(hk))
                .build();
    }

    private String formatHocKy(HocKy hk) {
        if (hk == null) {
            return null;
        }
        return "HK" + hk.getKyThu() + " - " + hk.getNamHoc();
    }
}
