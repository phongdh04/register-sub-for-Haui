package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.TimetableCourseResponse;
import com.example.demo.payload.response.TimetableResponse;
import com.example.demo.payload.response.TimetableSessionResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ITimetableService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements ITimetableService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public TimetableResponse getMyTimetable(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        HocKy hocKy = resolveHocKy(hocKyId);

        List<DangKyHocPhan> dangKyList = dangKyHocPhanRepository.findTimetableRegistrations(
                sinhVien.getIdSinhVien(),
                hocKy != null ? hocKy.getIdHocKy() : null
        );

        int tongTinChi = dangKyList.stream()
                .map(DangKyHocPhan::getLopHocPhan)
                .map(LopHocPhan::getHocPhan)
                .filter(Objects::nonNull)
                .map(HocPhan::getSoTinChi)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        List<TimetableCourseResponse> courses = dangKyList.stream()
                .map(this::toCourseResponse)
                .toList();

        HocKy hocKyResponse = hocKy;
        if (hocKyResponse == null && !dangKyList.isEmpty()) {
            hocKyResponse = dangKyList.get(0).getHocKy();
        }

        return TimetableResponse.builder()
                .idSinhVien(sinhVien.getIdSinhVien())
                .maSinhVien(sinhVien.getMaSinhVien())
                .hoTenSinhVien(sinhVien.getHoTen())
                .idHocKy(hocKyResponse != null ? hocKyResponse.getIdHocKy() : null)
                .tenHocKy(formatHocKy(hocKyResponse))
                .tongMonDangKy(courses.size())
                .tongTinChi(tongTinChi)
                .courses(courses)
                .build();
    }

    private HocKy resolveHocKy(Long hocKyId) {
        if (hocKyId != null) {
            return hocKyRepository.findById(hocKyId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ ID: " + hocKyId));
        }
        return hocKyRepository.findByTrangThaiHienHanhTrue().orElse(null);
    }

    private TimetableCourseResponse toCourseResponse(DangKyHocPhan dangKy) {
        LopHocPhan lhp = dangKy.getLopHocPhan();
        HocPhan hocPhan = lhp.getHocPhan();

        return TimetableCourseResponse.builder()
                .idDangKy(dangKy.getIdDangKy())
                .idLopHp(lhp.getIdLopHp())
                .maLopHp(lhp.getMaLopHp())
                .maHocPhan(hocPhan != null ? hocPhan.getMaHocPhan() : null)
                .tenHocPhan(hocPhan != null ? hocPhan.getTenHocPhan() : null)
                .soTinChi(hocPhan != null ? hocPhan.getSoTinChi() : null)
                .tenGiangVien(lhp.getGiangVien() != null ? lhp.getGiangVien().getTenGiangVien() : "Chưa phân công")
                .sessions(mapSessions(lhp.getThoiKhoaBieuJson()))
                .build();
    }

    private List<TimetableSessionResponse> mapSessions(List<Map<String, Object>> thoiKhoaBieuJson) {
        if (thoiKhoaBieuJson == null || thoiKhoaBieuJson.isEmpty()) {
            return List.of();
        }

        List<TimetableSessionResponse> sessions = new ArrayList<>();
        for (Map<String, Object> item : thoiKhoaBieuJson) {
            sessions.add(TimetableSessionResponse.builder()
                    .thu(toInteger(item.get("thu")))
                    .tiet(toStringSafe(item.get("tiet")))
                    .phong(toStringSafe(item.get("phong")))
                    .ngayBatDau(toStringSafe(item.get("ngay_bat_dau")))
                    .ngayKetThuc(toStringSafe(item.get("ngay_ket_thuc")))
                    .build());
        }
        return sessions;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String toStringSafe(Object value) {
        return value == null ? null : value.toString();
    }

    private String formatHocKy(HocKy hocKy) {
        if (hocKy == null) {
            return null;
        }
        return "HK" + hocKy.getKyThu() + " - " + hocKy.getNamHoc();
    }
}
