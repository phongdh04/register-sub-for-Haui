package com.example.demo.service.impl;

import com.example.demo.domain.entity.BangDiemMon;
import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.LichThi;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.YeuCauPhucKhao;
import com.example.demo.payload.request.CreateRetakeAppealRequest;
import com.example.demo.payload.request.LecturerRetakeAppealDecisionRequest;
import com.example.demo.payload.response.RetakeAppealRowResponse;
import com.example.demo.repository.BangDiemMonRepository;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.LichThiRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.YeuCauPhucKhaoRepository;
import com.example.demo.service.IRetakeAppealService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetakeAppealServiceImpl implements IRetakeAppealService {

    public static final String TT_CHO = "CHO_GV_XU_LY";
    public static final String TT_DONG_Y = "DONG_Y";
    public static final String TT_TU_CHOI = "TU_CHOI";

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final GiangVienRepository giangVienRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final YeuCauPhucKhaoRepository yeuCauPhucKhaoRepository;
    private final BangDiemMonRepository bangDiemMonRepository;
    private final LichThiRepository lichThiRepository;

    @Override
    @Transactional
    public RetakeAppealRowResponse submitAppeal(String studentUsername, CreateRetakeAppealRequest request) {
        User user = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + studentUsername));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        DangKyHocPhan dk = dangKyHocPhanRepository.findWithFullAppealContext(request.getIdDangKy())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đăng ký: " + request.getIdDangKy()));

        if (!Objects.equals(dk.getSinhVien().getIdSinhVien(), sv.getIdSinhVien())) {
            throw new AccessDeniedException("Không được nộp phúc khảo thay đăng ký của sinh viên khác.");
        }
        if (!"THANH_CONG".equals(dk.getTrangThaiDangKy())) {
            throw new IllegalArgumentException("Chỉ nộp phúc khảo khi đăng ký ở trạng thái THANH_CONG.");
        }
        if (!hasOfficialPublishedGrade(dk)) {
            throw new IllegalArgumentException("Chỉ nộp phúc khảo khi đã có điểm công bố chính thức (DA_CONG_BO hoặc bản ghi cũ không trạng thái).");
        }
        if (yeuCauPhucKhaoRepository.existsByDangKy_IdDangKyAndTrangThai(dk.getIdDangKy(), TT_CHO)) {
            throw new IllegalArgumentException("Đã có yêu cầu phúc khảo đang chờ giảng viên xử lý cho học phần này.");
        }

        BangDiemMon bdm = dk.getBangDiemMon();
        YeuCauPhucKhao y = YeuCauPhucKhao.builder()
                .dangKy(dk)
                .diemHe4LucNop(bdm != null ? bdm.getDiemHe4() : null)
                .lyDoSinhVien(request.getLyDoSinhVien().trim())
                .trangThai(TT_CHO)
                .ngayTao(LocalDateTime.now())
                .build();
        y = yeuCauPhucKhaoRepository.save(y);

        Map<Long, LichThi> lich = loadLichByLopIds(List.of(dk.getLopHocPhan().getIdLopHp()));
        return toRow(y, lich);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetakeAppealRowResponse> listMyAppeals(String studentUsername) {
        User user = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + studentUsername));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        List<YeuCauPhucKhao> list = yeuCauPhucKhaoRepository.findBySinhVienOrderByNgayTaoDesc(sv.getIdSinhVien());
        Map<Long, LichThi> lich = loadLichByLopIds(list.stream()
                .map(y -> y.getDangKy().getLopHocPhan().getIdLopHp())
                .distinct()
                .toList());
        return list.stream().map(y -> toRow(y, lich)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetakeAppealRowResponse> listForLecturer(String lecturerUsername, String trangThai) {
        GiangVien gv = resolveGiangVien(lecturerUsername);
        String tt = (trangThai == null || trangThai.isBlank()) ? null : trangThai.trim();
        List<YeuCauPhucKhao> list = yeuCauPhucKhaoRepository.findForLecturer(gv.getIdGiangVien(), tt);
        Map<Long, LichThi> lich = loadLichByLopIds(list.stream()
                .map(y -> y.getDangKy().getLopHocPhan().getIdLopHp())
                .distinct()
                .toList());
        return list.stream().map(y -> toRow(y, lich)).toList();
    }

    @Override
    @Transactional
    public RetakeAppealRowResponse processAppeal(String lecturerUsername, Long idYeuCau, LecturerRetakeAppealDecisionRequest request) {
        GiangVien gv = resolveGiangVien(lecturerUsername);
        YeuCauPhucKhao y = yeuCauPhucKhaoRepository.findByIdWithGraph(idYeuCau)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu: " + idYeuCau));
        DangKyHocPhan dk = y.getDangKy();
        assertLecturerOwnsLop(dk.getLopHocPhan(), gv);
        if (!TT_CHO.equals(y.getTrangThai())) {
            throw new IllegalArgumentException("Yêu cầu không còn ở trạng thái chờ xử lý.");
        }

        String qd = request.getQuyetDinh();
        if (TT_TU_CHOI.equals(qd)) {
            y.setTrangThai(TT_TU_CHOI);
            y.setGhiChuGiangVien(trimToNull(request.getGhiChuGiangVien()));
            y.setNgayXuLy(LocalDateTime.now());
            yeuCauPhucKhaoRepository.save(y);
        } else if (TT_DONG_Y.equals(qd)) {
            BigDecimal diem = request.getDiemSauPhucKhao();
            if (diem == null) {
                throw new IllegalArgumentException("Khi đồng ý phúc khảo, cần nhập điểm sau xử lý (hệ 4).");
            }
            diem = diem.setScale(2, RoundingMode.HALF_UP);
            if (diem.compareTo(BigDecimal.ZERO) < 0 || diem.compareTo(new BigDecimal("4.0")) > 0) {
                throw new IllegalArgumentException("Điểm hệ 4 phải nằm trong [0, 4].");
            }
            BangDiemMon bdm = bangDiemMonRepository.findByDangKyHocPhan_IdDangKy(dk.getIdDangKy())
                    .orElseThrow(() -> new IllegalStateException("Thiếu bản ghi bảng điểm."));
            bdm.setDiemHe4(diem);
            bdm.setDiemChu(letterGrade4(diem));
            bdm.setTrangThai("DA_CONG_BO");
            bangDiemMonRepository.save(bdm);

            y.setTrangThai(TT_DONG_Y);
            y.setDiemSauXuLy(diem);
            y.setGhiChuGiangVien(trimToNull(request.getGhiChuGiangVien()));
            y.setNgayXuLy(LocalDateTime.now());
            yeuCauPhucKhaoRepository.save(y);
        } else {
            throw new IllegalArgumentException("Quyết định không hợp lệ.");
        }

        Map<Long, LichThi> lich = loadLichByLopIds(List.of(dk.getLopHocPhan().getIdLopHp()));
        return toRow(y, lich);
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private boolean hasOfficialPublishedGrade(DangKyHocPhan dk) {
        BangDiemMon bdm = dk.getBangDiemMon();
        if (bdm == null || bdm.getDiemHe4() == null) {
            return false;
        }
        String t = bdm.getTrangThai();
        return t == null || "DA_CONG_BO".equals(t);
    }

    private GiangVien resolveGiangVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        return giangVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ giảng viên."));
    }

    private void assertLecturerOwnsLop(LopHocPhan lop, GiangVien gv) {
        if (lop.getGiangVien() == null || !Objects.equals(lop.getGiangVien().getIdGiangVien(), gv.getIdGiangVien())) {
            throw new AccessDeniedException("Bạn không phải giảng viên phụ trách lớp này.");
        }
    }

    private Map<Long, LichThi> loadLichByLopIds(List<Long> lopIds) {
        if (lopIds == null || lopIds.isEmpty()) {
            return Map.of();
        }
        return lichThiRepository.findByLopHocPhan_IdLopHpIn(lopIds).stream()
                .collect(Collectors.toMap(l -> l.getLopHocPhan().getIdLopHp(), Function.identity(), (a, b) -> a));
    }

    private RetakeAppealRowResponse toRow(YeuCauPhucKhao y, Map<Long, LichThi> lichByLop) {
        DangKyHocPhan d = y.getDangKy();
        SinhVien sv = d.getSinhVien();
        var lhp = d.getLopHocPhan();
        var hp = lhp.getHocPhan();
        HocKy hk = d.getHocKy();
        BangDiemMon bdm = d.getBangDiemMon();
        LichThi lt = lichByLop != null ? lichByLop.get(lhp.getIdLopHp()) : null;

        return RetakeAppealRowResponse.builder()
                .idYeuCau(y.getIdYeuCau())
                .trangThai(y.getTrangThai())
                .ngayTao(y.getNgayTao())
                .ngayXuLy(y.getNgayXuLy())
                .lyDoSinhVien(y.getLyDoSinhVien())
                .ghiChuGiangVien(y.getGhiChuGiangVien())
                .diemHe4LucNop(y.getDiemHe4LucNop())
                .diemSauXuLy(y.getDiemSauXuLy())
                .idDangKy(d.getIdDangKy())
                .maSinhVien(sv.getMaSinhVien())
                .hoTenSinhVien(sv.getHoTen())
                .maLopHanhChinh(sv.getLop() != null ? sv.getLop().getMaLop() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                .maLopHp(lhp.getMaLopHp())
                .hocKyLabel(formatHocKy(hk))
                .diemHe4HienTai(bdm != null ? bdm.getDiemHe4() : null)
                .ngayThi(lt != null && lt.getNgayThi() != null ? lt.getNgayThi().toString() : null)
                .caThi(lt != null ? lt.getCaThi() : null)
                .phongThi(lt != null ? lt.getPhongThi() : null)
                .build();
    }

    private String formatHocKy(HocKy hk) {
        if (hk == null) {
            return null;
        }
        return "HK" + hk.getKyThu() + " - " + hk.getNamHoc();
    }

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
