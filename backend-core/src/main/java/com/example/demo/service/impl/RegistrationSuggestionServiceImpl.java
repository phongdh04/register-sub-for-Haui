package com.example.demo.service.impl;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import com.example.demo.domain.entity.CtdtHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.CourseSearchResponse;
import com.example.demo.payload.response.RegistrationSuggestionResponse;
import com.example.demo.repository.ChuongTrinhDaoTaoRepository;
import com.example.demo.repository.CtdtHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ICourseSearchService;
import com.example.demo.service.IRegistrationSuggestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationSuggestionServiceImpl implements IRegistrationSuggestionService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final ICourseSearchService courseSearchService;

    @Override
    @Transactional(readOnly = true)
    public RegistrationSuggestionResponse suggestForCurrentStudent(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findWithProfileByTaiKhoanId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        Lop lop = sv.getLop();
        if (lop == null) {
            throw new EntityNotFoundException("Sinh viên chưa có lớp hành chính.");
        }
        if (lop.getNganhDaoTao() == null) {
            throw new EntityNotFoundException("Lớp hành chính chưa gắn ngành đào tạo.");
        }
        Integer namNhap = lop.getNamNhapHoc();
        if (namNhap == null) {
            throw new IllegalArgumentException("Lớp hành chính chưa có năm nhập học — không suy ra khóa/lộ trình.");
        }

        HocKy hk = resolveHocKy(hocKyId);
        int yearStart = parseYearStartNamHoc(hk.getNamHoc());
        int kyThu = hk.getKyThu() != null ? hk.getKyThu() : 1;
        int thuTuHocKy = estimateStudySemesterIndex(yearStart, namNhap, kyThu);

        ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoRepository
                .findLatestByNganh(lop.getNganhDaoTao().getIdNganh())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy CTĐT cho ngành của lớp bạn."));

        List<CtdtHocPhan> mapping = ctdtHocPhanRepository.findAllByCtdtId(ctdt.getIdCtdt());
        Set<Long> hocPhanIds = mapping.stream()
                .filter(c -> matchesCtdtRowForRegistration(c, thuTuHocKy))
                .map(c -> c.getHocPhan().getIdHocPhan())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<LopHocPhan> lops;
        if (hocPhanIds.isEmpty()) {
            lops = List.of();
        } else {
            lops = lopHocPhanRepository.findOpenByHocKyAndHocPhanIds(hk.getIdHocKy(), hocPhanIds, "DANG_MO");
        }
        List<CourseSearchResponse> courses = courseSearchService.mapLopHocPhanList(lops);

        String moTa = String.format(
                "Ước lượng thứ tự học kỳ trong CTĐT = (năm bắt đầu năm học %d − năm nhập học %d) × 2 + kỳ thứ (%d), "
                        + "sau đó lọc môn CTĐT có hoc_ky_goi_y trùng kỳ này hoặc kỳ kế, và môn tự chọn chưa gắn kỳ gợi ý.",
                yearStart, namNhap, kyThu);

        return RegistrationSuggestionResponse.builder()
                .namNhapHoc(namNhap)
                .maLop(lop.getMaLop())
                .tenLop(lop.getTenLop())
                .nhanKhoa("K" + (namNhap % 100))
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(formatHocKy(hk))
                .kyThuDangKy(kyThu)
                .namHocDangKy(hk.getNamHoc())
                .thuTuHocKyUocLuong(thuTuHocKy)
                .moTaCachTinh(moTa)
                .lopDeXuat(courses)
                .build();
    }

    private static boolean matchesCtdtRowForRegistration(CtdtHocPhan c, int thuTuHocKy) {
        Integer g = c.getHocKyGoiY();
        if (g != null) {
            return g == thuTuHocKy || g == thuTuHocKy + 1;
        }
        return Boolean.FALSE.equals(c.getBatBuoc());
    }

    /**
     * Giả định 2 học kỳ chính / năm học; kỳ hè (ky_thu = 3) gom vào học kỳ 2 của năm đó.
     */
    private static int estimateStudySemesterIndex(int yearStartNamHoc, int namNhapHoc, int kyThu) {
        int delta = Math.max(0, yearStartNamHoc - namNhapHoc);
        int ky = Math.max(1, kyThu);
        if (ky >= 3) {
            ky = 2;
        }
        return Math.max(1, delta * 2 + ky);
    }

    private static int parseYearStartNamHoc(String namHoc) {
        if (namHoc == null || namHoc.isBlank()) {
            throw new IllegalArgumentException("Học kỳ thiếu năm học.");
        }
        String[] parts = namHoc.trim().split("-");
        try {
            return Integer.parseInt(parts[0].trim());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Năm học không đúng định dạng (vd 2024-2025).");
        }
    }

    private HocKy resolveHocKy(Long hocKyId) {
        if (hocKyId != null) {
            return hocKyRepository.findById(hocKyId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        }
        return hocKyRepository.findByTrangThaiHienHanhTrue()
                .orElseThrow(() -> new EntityNotFoundException("Chưa có học kỳ hiện hành."));
    }

    private static String formatHocKy(HocKy hk) {
        return "HK" + hk.getKyThu() + " - " + hk.getNamHoc();
    }
}
