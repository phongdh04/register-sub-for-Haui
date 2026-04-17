package com.example.demo.service.impl;

import com.example.demo.domain.entity.BangDiemMon;
import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.TranscriptLineResponse;
import com.example.demo.payload.response.TranscriptResponse;
import com.example.demo.payload.response.TranscriptSemesterResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ITranscriptService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranscriptServiceImpl implements ITranscriptService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public TranscriptResponse getMyTranscript(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        List<DangKyHocPhan> rows = dangKyHocPhanRepository.findTranscriptRows(
                sinhVien.getIdSinhVien(),
                hocKyId
        );

        Map<Long, List<DangKyHocPhan>> byHocKy = new LinkedHashMap<>();
        for (DangKyHocPhan d : rows) {
            Long hkId = d.getHocKy().getIdHocKy();
            byHocKy.computeIfAbsent(hkId, k -> new ArrayList<>()).add(d);
        }

        List<TranscriptSemesterResponse> hocKys = new ArrayList<>();
        for (Map.Entry<Long, List<DangKyHocPhan>> entry : byHocKy.entrySet()) {
            List<DangKyHocPhan> semesterRows = entry.getValue();
            HocKy hk = semesterRows.get(0).getHocKy();

            List<TranscriptLineResponse> lines = new ArrayList<>();
            int tinDangKy = 0;
            int tinCoDiem = 0;

            for (DangKyHocPhan d : semesterRows) {
                HocPhan hp = d.getLopHocPhan().getHocPhan();
                BangDiemMon bdm = d.getBangDiemMon();
                Integer tc = hp != null ? hp.getSoTinChi() : null;
                if (tc != null) {
                    tinDangKy += tc;
                }
                boolean coDiem = bdm != null && bdm.getDiemHe4() != null;
                if (coDiem && tc != null) {
                    tinCoDiem += tc;
                }
                lines.add(TranscriptLineResponse.builder()
                        .idDangKy(d.getIdDangKy())
                        .maLopHp(d.getLopHocPhan() != null ? d.getLopHocPhan().getMaLopHp() : null)
                        .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                        .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                        .soTinChi(tc)
                        .diemHe4(bdm != null ? bdm.getDiemHe4() : null)
                        .diemChu(bdm != null ? bdm.getDiemChu() : null)
                        .daCoDiem(coDiem)
                        .build());
            }

            BigDecimal gpaKy = weightedGpa(semesterRows);
            hocKys.add(TranscriptSemesterResponse.builder()
                    .idHocKy(hk.getIdHocKy())
                    .tenHocKy(formatHocKy(hk))
                    .tongTinChiDangKy(tinDangKy)
                    .tongTinChiCoDiem(tinCoDiem)
                    .gpaHocKy(gpaKy)
                    .monHoc(lines)
                    .build());
        }

        int tongTinChiDangKy = rows.stream()
                .map(d -> d.getLopHocPhan().getHocPhan())
                .filter(hp -> hp != null && hp.getSoTinChi() != null)
                .mapToInt(HocPhan::getSoTinChi)
                .sum();

        int tongTinChiCoDiem = 0;
        for (DangKyHocPhan d : rows) {
            HocPhan hp = d.getLopHocPhan().getHocPhan();
            BangDiemMon bdm = d.getBangDiemMon();
            if (bdm != null && bdm.getDiemHe4() != null && hp != null && hp.getSoTinChi() != null) {
                tongTinChiCoDiem += hp.getSoTinChi();
            }
        }

        BigDecimal gpaTichLuy = weightedGpa(rows);

        return TranscriptResponse.builder()
                .idSinhVien(sinhVien.getIdSinhVien())
                .maSinhVien(sinhVien.getMaSinhVien())
                .hoTenSinhVien(sinhVien.getHoTen())
                .gpaTichLuy(gpaTichLuy)
                .tongTinChiDangKy(tongTinChiDangKy)
                .tongTinChiCoDiem(tongTinChiCoDiem)
                .hocKys(hocKys)
                .build();
    }

    private BigDecimal weightedGpa(List<DangKyHocPhan> rows) {
        BigDecimal sum = BigDecimal.ZERO;
        int credits = 0;
        for (DangKyHocPhan d : rows) {
            HocPhan hp = d.getLopHocPhan().getHocPhan();
            if (hp == null || hp.getSoTinChi() == null) {
                continue;
            }
            BangDiemMon bdm = d.getBangDiemMon();
            if (bdm == null || bdm.getDiemHe4() == null) {
                continue;
            }
            sum = sum.add(bdm.getDiemHe4().multiply(BigDecimal.valueOf(hp.getSoTinChi())));
            credits += hp.getSoTinChi();
        }
        if (credits == 0) {
            return null;
        }
        return sum.divide(BigDecimal.valueOf(credits), 2, RoundingMode.HALF_UP);
    }

    private String formatHocKy(HocKy hk) {
        if (hk == null) {
            return null;
        }
        return "HK" + hk.getKyThu() + " - " + hk.getNamHoc();
    }
}
