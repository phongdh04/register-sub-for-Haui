package com.example.demo.service.impl;

import com.example.demo.domain.entity.BuoiDiemDanh;
import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.DiemDanhDangKy;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.AttendanceCheckInRequest;
import com.example.demo.payload.request.CreateAttendanceSessionRequest;
import com.example.demo.payload.request.PatchAttendanceRowRequest;
import com.example.demo.payload.response.AttendanceCheckInResponse;
import com.example.demo.payload.response.AttendanceRowResponse;
import com.example.demo.payload.response.AttendanceSessionResponse;
import com.example.demo.payload.response.LecturerTeachingClassResponse;
import com.example.demo.repository.BuoiDiemDanhRepository;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.DiemDanhDangKyRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IAttendanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {

    private static final Set<String> ALLOWED_TRANG_THAI = Set.of("CO_MAT", "VANG", "PHEP");
    private static final List<String> DK_DANG_KY_HOP_LE = List.of("THANH_CONG", "CHO_DUYET");

    private final UserRepository userRepository;
    private final GiangVienRepository giangVienRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final BuoiDiemDanhRepository buoiDiemDanhRepository;
    private final DiemDanhDangKyRepository diemDanhDangKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final SinhVienRepository sinhVienRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LecturerTeachingClassResponse> listMyTeachingClasses(String username) {
        GiangVien gv = resolveGiangVien(username);
        return lopHocPhanRepository.findTeachingClassesForGiangVien(gv.getIdGiangVien()).stream()
                .map(this::toTeachingClass)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceSessionResponse createOrGetSession(String username, Long idLopHp, CreateAttendanceSessionRequest request) {
        GiangVien gv = resolveGiangVien(username);
        LopHocPhan lop = lopHocPhanRepository.findWithGiangVienForAttendance(idLopHp)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần: " + idLopHp));
        assertLecturerOwnsLop(lop, gv);

        LocalDate ngay = request != null && request.getNgayBuoi() != null
                ? request.getNgayBuoi()
                : LocalDate.now();

        return buoiDiemDanhRepository.findByLopHocPhan_IdLopHpAndNgayBuoi(idLopHp, ngay)
                .map(b -> buildSessionResponse(b.getIdBuoi()))
                .orElseGet(() -> {
                    BuoiDiemDanh buoi = BuoiDiemDanh.builder()
                            .lopHocPhan(lop)
                            .ngayBuoi(ngay)
                            .publicToken(UUID.randomUUID().toString().replace("-", ""))
                            .build();
                    buoi = buoiDiemDanhRepository.save(buoi);

                    List<DangKyHocPhan> dangKys = dangKyHocPhanRepository
                            .findByLopHocPhan_IdLopHpAndTrangThaiDangKyIn(idLopHp, DK_DANG_KY_HOP_LE);
                    for (DangKyHocPhan dk : dangKys) {
                        diemDanhDangKyRepository.save(DiemDanhDangKy.builder()
                                .buoiDiemDanh(buoi)
                                .dangKyHocPhan(dk)
                                .trangThai("VANG")
                                .build());
                    }
                    return buildSessionResponse(buoi.getIdBuoi());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSessionResponse getSessionForLecturer(String username, Long idBuoi) {
        GiangVien gv = resolveGiangVien(username);
        BuoiDiemDanh buoi = buoiDiemDanhRepository.findWithLopById(idBuoi)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy buổi điểm danh: " + idBuoi));
        assertLecturerOwnsLop(buoi.getLopHocPhan(), gv);
        return buildSessionResponse(idBuoi);
    }

    @Override
    @Transactional
    public AttendanceRowResponse patchRowForLecturer(String username, Long idDiemDanh, PatchAttendanceRowRequest request) {
        GiangVien gv = resolveGiangVien(username);
        String tt = request.getTrangThai() != null ? request.getTrangThai().trim().toUpperCase() : "";
        if (!ALLOWED_TRANG_THAI.contains(tt)) {
            throw new IllegalArgumentException("trangThai không hợp lệ (CO_MAT, VANG, PHEP).");
        }

        DiemDanhDangKy row = diemDanhDangKyRepository.findWithBuoiAndLopForAuth(idDiemDanh)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dòng điểm danh: " + idDiemDanh));
        assertLecturerOwnsLop(row.getBuoiDiemDanh().getLopHocPhan(), gv);

        row.setTrangThai(tt);
        row.setThoiGianCapNhat(LocalDateTime.now());
        diemDanhDangKyRepository.save(row);
        return toRowResponse(row);
    }

    @Override
    @Transactional
    public AttendanceCheckInResponse studentCheckIn(String username, AttendanceCheckInRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        BuoiDiemDanh buoi = buoiDiemDanhRepository.findByPublicTokenWithLop(request.getToken().trim())
                .orElseThrow(() -> new EntityNotFoundException("Mã buổi học (token) không hợp lệ."));
        LopHocPhan lop = buoi.getLopHocPhan();

        DangKyHocPhan dk = dangKyHocPhanRepository
                .findFirstByLopHocPhan_IdLopHpAndSinhVien_IdSinhVienAndTrangThaiDangKyIn(
                        lop.getIdLopHp(), sv.getIdSinhVien(), DK_DANG_KY_HOP_LE)
                .orElseThrow(() -> new IllegalArgumentException("Bạn không đăng ký lớp này hoặc trạng thái đăng ký không hợp lệ."));

        if (!Objects.equals(dk.getHocKy().getIdHocKy(), lop.getHocKy().getIdHocKy())) {
            throw new IllegalArgumentException("Đăng ký không khớp học kỳ của lớp.");
        }

        DiemDanhDangKy row = diemDanhDangKyRepository
                .findByBuoiDiemDanh_IdBuoiAndDangKyHocPhan_IdDangKy(buoi.getIdBuoi(), dk.getIdDangKy())
                .orElseGet(() -> DiemDanhDangKy.builder()
                        .buoiDiemDanh(buoi)
                        .dangKyHocPhan(dk)
                        .trangThai("VANG")
                        .build());

        row.setTrangThai("CO_MAT");
        row.setThoiGianCapNhat(LocalDateTime.now());
        diemDanhDangKyRepository.save(row);

        return AttendanceCheckInResponse.builder()
                .maLopHp(lop.getMaLopHp())
                .tenHocPhan(lop.getHocPhan().getTenHocPhan())
                .ngayBuoi(buoi.getNgayBuoi())
                .trangThai(row.getTrangThai())
                .build();
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

    private LecturerTeachingClassResponse toTeachingClass(LopHocPhan l) {
        var hk = l.getHocKy();
        String hkLabel = hk.getNamHoc() + " — Kỳ " + hk.getKyThu();
        return LecturerTeachingClassResponse.builder()
                .idLopHp(l.getIdLopHp())
                .maLopHp(l.getMaLopHp())
                .idHocPhan(l.getHocPhan().getIdHocPhan())
                .maHocPhan(l.getHocPhan().getMaHocPhan())
                .tenHocPhan(l.getHocPhan().getTenHocPhan())
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(hkLabel)
                .build();
    }

    private AttendanceSessionResponse buildSessionResponse(Long idBuoi) {
        BuoiDiemDanh buoi = buoiDiemDanhRepository.findWithLopById(idBuoi)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy buổi điểm danh: " + idBuoi));
        LopHocPhan lop = buoi.getLopHocPhan();
        List<DiemDanhDangKy> rows = diemDanhDangKyRepository.findByBuoiWithSinhVien(idBuoi);

        int coMat = 0;
        int vang = 0;
        int phep = 0;
        for (DiemDanhDangKy r : rows) {
            switch (r.getTrangThai()) {
                case "CO_MAT" -> coMat++;
                case "PHEP" -> phep++;
                default -> vang++;
            }
        }

        String token = buoi.getPublicToken();
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=280x280&data="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        return AttendanceSessionResponse.builder()
                .idBuoi(buoi.getIdBuoi())
                .idLopHp(lop.getIdLopHp())
                .maLopHp(lop.getMaLopHp())
                .tenHocPhan(lop.getHocPhan().getTenHocPhan())
                .ngayBuoi(buoi.getNgayBuoi())
                .publicToken(token)
                .qrImageUrl(qrUrl)
                .tongSo(rows.size())
                .coMat(coMat)
                .vang(vang)
                .phep(phep)
                .rows(rows.stream().map(this::toRowResponse).collect(Collectors.toList()))
                .build();
    }

    private AttendanceRowResponse toRowResponse(DiemDanhDangKy d) {
        SinhVien sv = d.getDangKyHocPhan().getSinhVien();
        return AttendanceRowResponse.builder()
                .idDiemDanh(d.getIdDiemDanh())
                .idDangKy(d.getDangKyHocPhan().getIdDangKy())
                .maSinhVien(sv.getMaSinhVien())
                .hoTen(sv.getHoTen())
                .trangThai(d.getTrangThai())
                .thoiGianCapNhat(d.getThoiGianCapNhat())
                .build();
    }
}
