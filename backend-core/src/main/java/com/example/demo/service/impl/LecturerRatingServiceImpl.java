package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.DanhGiaGiangVien;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.LecturerRatingSubmitRequest;
import com.example.demo.payload.response.LecturerRatingListResponse;
import com.example.demo.payload.response.LecturerRatingRowResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.DanhGiaGiangVienRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ILecturerRatingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerRatingServiceImpl implements ILecturerRatingService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final DanhGiaGiangVienRepository danhGiaGiangVienRepository;

    @Override
    @Transactional(readOnly = true)
    public LecturerRatingListResponse listMine(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        HocKy hk = resolveHocKy(hocKyId);

        List<DangKyHocPhan> dks = dangKyHocPhanRepository.findRegisteredCoursesInSemester(
                sv.getIdSinhVien(), hk.getIdHocKy());

        List<Long> dkIds = dks.stream().map(DangKyHocPhan::getIdDangKy).toList();
        Map<Long, DanhGiaGiangVien> byDk = dkIds.isEmpty()
                ? Map.of()
                : danhGiaGiangVienRepository.findByDangKy_IdDangKyIn(dkIds).stream()
                        .collect(Collectors.toMap(d -> d.getDangKy().getIdDangKy(), d -> d, (a, b) -> a));

        List<LecturerRatingRowResponse> rows = new ArrayList<>();
        for (DangKyHocPhan d : dks) {
            LopHocPhan lhp = d.getLopHocPhan();
            HocPhan hp = lhp.getHocPhan();
            GiangVien gv = lhp.getGiangVien();
            DanhGiaGiangVien dg = byDk.get(d.getIdDangKy());
            boolean hasGv = gv != null;
            rows.add(LecturerRatingRowResponse.builder()
                    .idDangKy(d.getIdDangKy())
                    .idGiangVien(hasGv ? gv.getIdGiangVien() : null)
                    .maGiangVien(hasGv ? gv.getMaGiangVien() : null)
                    .tenGiangVien(hasGv ? gv.getTenGiangVien() : null)
                    .maLopHp(lhp.getMaLopHp())
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .coGiangVien(hasGv)
                    .daDanhGia(dg != null)
                    .diemTong(dg != null ? dg.getDiemTong() : null)
                    .binhLuan(dg != null ? dg.getBinhLuan() : null)
                    .build());
        }

        return LecturerRatingListResponse.builder()
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(formatHocKy(hk))
                .rows(rows)
                .build();
    }

    @Override
    @Transactional
    public LecturerRatingRowResponse submit(String username, LecturerRatingSubmitRequest body) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        DangKyHocPhan d = dangKyHocPhanRepository.findById(body.getIdDangKy())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đăng ký."));
        if (!d.getSinhVien().getIdSinhVien().equals(sv.getIdSinhVien())) {
            throw new IllegalArgumentException("Bạn không được phép đánh giá đăng ký này.");
        }
        if (!"THANH_CONG".equals(d.getTrangThaiDangKy()) && !"CHO_DUYET".equals(d.getTrangThaiDangKy())) {
            throw new IllegalArgumentException("Chỉ đánh giá khi đăng ký còn hiệu lực.");
        }
        LopHocPhan lhp = d.getLopHocPhan();
        if (lhp.getGiangVien() == null) {
            throw new IllegalArgumentException("Lớp học phần chưa gán giảng viên, không thể đánh giá.");
        }

        DanhGiaGiangVien entity = danhGiaGiangVienRepository.findByDangKy_IdDangKy(d.getIdDangKy())
                .orElse(DanhGiaGiangVien.builder().dangKy(d).build());
        entity.setDiemTong(body.getDiemTong());
        entity.setBinhLuan(body.getBinhLuan());
        entity = danhGiaGiangVienRepository.save(entity);

        GiangVien gv = lhp.getGiangVien();
        HocPhan hp = lhp.getHocPhan();
        return LecturerRatingRowResponse.builder()
                .idDangKy(d.getIdDangKy())
                .idGiangVien(gv.getIdGiangVien())
                .maGiangVien(gv.getMaGiangVien())
                .tenGiangVien(gv.getTenGiangVien())
                .maLopHp(lhp.getMaLopHp())
                .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .coGiangVien(true)
                .daDanhGia(true)
                .diemTong(entity.getDiemTong())
                .binhLuan(entity.getBinhLuan())
                .build();
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

    private String formatHocKy(HocKy hk) {
        if (hk == null) {
            return null;
        }
        return "HK" + hk.getKyThu() + " - " + hk.getNamHoc();
    }
}
