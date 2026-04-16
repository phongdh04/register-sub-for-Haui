package com.example.demo.service.impl;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import com.example.demo.domain.entity.CtdtHocPhan;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.DegreeAuditBlockResponse;
import com.example.demo.payload.response.DegreeAuditCourseItemResponse;
import com.example.demo.payload.response.DegreeAuditResponse;
import com.example.demo.repository.ChuongTrinhDaoTaoRepository;
import com.example.demo.repository.CtdtHocPhanRepository;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IDegreeAuditService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class DegreeAuditServiceImpl implements IDegreeAuditService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    private static class BlockAcc {
        int tongTinChi = 0;
        int tinChiDaHoanThanh = 0;
        final List<DegreeAuditCourseItemResponse> hocPhans = new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public DegreeAuditResponse getMyDegreeAudit(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        if (sinhVien.getLop() == null || sinhVien.getLop().getNganhDaoTao() == null) {
            throw new EntityNotFoundException("Sinh viên chưa có thông tin ngành đào tạo.");
        }

        NganhDaoTao nganh = sinhVien.getLop().getNganhDaoTao();
        ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoRepository.findLatestByNganh(nganh.getIdNganh())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy CTĐT cho ngành: " + nganh.getTenNganh()));

        List<CtdtHocPhan> mapping = ctdtHocPhanRepository.findAllByCtdtId(ctdt.getIdCtdt());

        Set<String> completedCourseCodes = Set.copyOf(
                dangKyHocPhanRepository.findCompletedCourseCodes(sinhVien.getIdSinhVien())
        );

        Map<String, BlockAcc> blocks = new LinkedHashMap<>();

        int tongTinChiDaHoanThanh = 0;

        for (CtdtHocPhan item : mapping) {
            HocPhan hp = item.getHocPhan();
            boolean done = hp != null && completedCourseCodes.contains(hp.getMaHocPhan());

            DegreeAuditCourseItemResponse course = DegreeAuditCourseItemResponse.builder()
                    .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .soTinChi(hp != null ? hp.getSoTinChi() : null)
                    .loaiMon(hp != null ? hp.getLoaiMon() : null)
                    .batBuoc(Boolean.TRUE.equals(item.getBatBuoc()))
                    .hocKyGoiY(item.getHocKyGoiY())
                    .daHoanThanh(done)
                    .build();

            String blockKey = item.getKhoiKienThuc();
            BlockAcc acc = blocks.computeIfAbsent(blockKey, key -> new BlockAcc());

            int tc = hp != null && hp.getSoTinChi() != null ? hp.getSoTinChi() : 0;

            acc.tongTinChi += tc;

            if (done) {
                tongTinChiDaHoanThanh += tc;
                acc.tinChiDaHoanThanh += tc;
            }

            acc.hocPhans.add(course);
        }

        List<DegreeAuditBlockResponse> khois = blocks.entrySet().stream()
                .map(entry -> DegreeAuditBlockResponse.builder()
                        .khoiKienThuc(entry.getKey())
                        .tongTinChi(entry.getValue().tongTinChi)
                        .tinChiDaHoanThanh(entry.getValue().tinChiDaHoanThanh)
                        .hocPhans(entry.getValue().hocPhans)
                        .build()
                )
                .toList();

        String tenKhoa = nganh.getKhoa() != null ? nganh.getKhoa().getTenKhoa() : null;

        return DegreeAuditResponse.builder()
                .idSinhVien(sinhVien.getIdSinhVien())
                .maSinhVien(sinhVien.getMaSinhVien())
                .hoTenSinhVien(sinhVien.getHoTen())
                .idNganh(nganh.getIdNganh())
                .maNganh(nganh.getMaNganh())
                .tenNganh(nganh.getTenNganh())
                .heDaoTao(nganh.getHeDaoTao())
                .tenKhoa(tenKhoa)
                .idCtdt(ctdt.getIdCtdt())
                .namApDung(ctdt.getNamApDung())
                .tongSoTinChiToanKhoa(ctdt.getTongSoTinChi())
                .mucTieu(ctdt.getMucTieu())
                .thoiGianGiangDay(ctdt.getThoiGianGiangDay())
                .tongTinChiDaHoanThanh(tongTinChiDaHoanThanh)
                .khois(khois)
                .build();
    }
}

