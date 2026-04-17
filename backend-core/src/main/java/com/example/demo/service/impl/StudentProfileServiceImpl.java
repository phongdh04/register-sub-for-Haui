package com.example.demo.service.impl;

import com.example.demo.domain.entity.CoVanHocTap;
import com.example.demo.domain.entity.HoSoSinhVien;
import com.example.demo.domain.entity.Khoa;
import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.StudentContactPatchRequest;
import com.example.demo.payload.response.ProfileProcedureResponse;
import com.example.demo.payload.response.StudentProfileResponse;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IStudentProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements IStudentProfileService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findWithProfileByTaiKhoanId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        return toResponse(sv);
    }

    @Override
    @Transactional
    public StudentProfileResponse patchMyContact(String username, StudentContactPatchRequest request) {
        if (isAllBlank(request)) {
            throw new IllegalArgumentException("Gửi ít nhất một trường cần cập nhật (email, sdt, diaChi).");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findWithProfileByTaiKhoanId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        HoSoSinhVien ho = sv.getHoSoSinhVien();
        if (ho == null) {
            ho = HoSoSinhVien.builder()
                    .sinhVien(sv)
                    .build();
            sv.setHoSoSinhVien(ho);
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String em = request.getEmail().trim();
            if (!em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("Email không hợp lệ.");
            }
            ho.setEmail(em);
        }
        if (request.getSdt() != null && !request.getSdt().isBlank()) {
            ho.setSdt(request.getSdt().trim());
        }
        if (request.getDiaChi() != null && !request.getDiaChi().isBlank()) {
            ho.setDiaChi(request.getDiaChi().trim());
        }

        sinhVienRepository.save(sv);
        return toResponse(sinhVienRepository.findWithProfileByTaiKhoanId(user.getId()).orElse(sv));
    }

    private static boolean isAllBlank(StudentContactPatchRequest r) {
        return (r.getEmail() == null || r.getEmail().isBlank())
                && (r.getSdt() == null || r.getSdt().isBlank())
                && (r.getDiaChi() == null || r.getDiaChi().isBlank());
    }

    private StudentProfileResponse toResponse(SinhVien sv) {
        Lop lop = sv.getLop();
        NganhDaoTao nganh = lop != null ? lop.getNganhDaoTao() : null;
        Khoa khoa = nganh != null ? nganh.getKhoa() : null;
        CoVanHocTap cv = sv.getCoVanHocTap();
        HoSoSinhVien ho = sv.getHoSoSinhVien();

        return StudentProfileResponse.builder()
                .idSinhVien(sv.getIdSinhVien())
                .maSinhVien(sv.getMaSinhVien())
                .hoTen(sv.getHoTen())
                .idLop(lop != null ? lop.getIdLop() : null)
                .maLop(lop != null ? lop.getMaLop() : null)
                .tenLop(lop != null ? lop.getTenLop() : null)
                .namNhapHoc(lop != null ? lop.getNamNhapHoc() : null)
                .idNganh(nganh != null ? nganh.getIdNganh() : null)
                .maNganh(nganh != null ? nganh.getMaNganh() : null)
                .tenNganh(nganh != null ? nganh.getTenNganh() : null)
                .heDaoTao(nganh != null ? nganh.getHeDaoTao() : null)
                .idKhoa(khoa != null ? khoa.getIdKhoa() : null)
                .maKhoa(khoa != null ? khoa.getMaKhoa() : null)
                .tenKhoa(khoa != null ? khoa.getTenKhoa() : null)
                .idCoVan(cv != null ? cv.getIdCoVan() : null)
                .tenCoVan(cv != null ? cv.getTenCoVan() : null)
                .emailCoVan(cv != null ? cv.getEmail() : null)
                .sdtCoVan(cv != null ? cv.getSdt() : null)
                .email(ho != null ? ho.getEmail() : null)
                .sdt(ho != null ? ho.getSdt() : null)
                .diaChi(ho != null ? ho.getDiaChi() : null)
                .ngaySinh(ho != null ? ho.getNgaySinh() : null)
                .gioiTinh(ho != null ? ho.getGioiTinh() : null)
                .quocTich(ho != null ? ho.getQuocTich() : null)
                .danToc(ho != null ? ho.getDanToc() : null)
                .tonGiao(ho != null ? ho.getTonGiao() : null)
                .soCccd(ho != null ? ho.getSoCccd() : null)
                .ngayCapCccd(ho != null ? ho.getNgayCapCccd() : null)
                .noiCapCccd(ho != null ? ho.getNoiCapCccd() : null)
                .maTheBhyt(ho != null ? ho.getMaTheBhyt() : null)
                .tenNganHang(ho != null ? ho.getTenNganHang() : null)
                .soTkNganHang(ho != null ? ho.getSoTkNganHang() : null)
                .thuTucTrucTuyen(demoProcedures())
                .build();
    }

    private List<ProfileProcedureResponse> demoProcedures() {
        List<ProfileProcedureResponse> list = new ArrayList<>();
        list.add(ProfileProcedureResponse.builder()
                .ma("XAC_NHAN_THONG_TIN")
                .ten("Xác nhận thông tin cá nhân")
                .trangThai("HOAN_THANH")
                .ghiChu("Theo dữ liệu hệ thống EduPort")
                .build());
        list.add(ProfileProcedureResponse.builder()
                .ma("CAP_NHAT_ANH_THE")
                .ten("Cập nhật ảnh thẻ / CCCD")
                .trangThai("CHO_BO_SUNG")
                .ghiChu("Vui lòng tải ảnh rõ nét khi tính năng upload được bật")
                .build());
        list.add(ProfileProcedureResponse.builder()
                .ma("BHYT_DOT")
                .ten("Đăng ký BHYT theo đợt")
                .trangThai("KHONG_AP_DUNG")
                .ghiChu("Theo thông báo Phòng CTSV từng học kỳ")
                .build());
        return list;
    }
}
