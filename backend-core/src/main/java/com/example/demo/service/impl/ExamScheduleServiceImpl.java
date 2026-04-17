package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LichThi;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhieuDuThi;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.ExamScheduleResponse;
import com.example.demo.payload.response.ExamScheduleRowResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LichThiRepository;
import com.example.demo.repository.PhieuDuThiRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IExamScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamScheduleServiceImpl implements IExamScheduleService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LichThiRepository lichThiRepository;
    private final PhieuDuThiRepository phieuDuThiRepository;

    @Override
    @Transactional(readOnly = true)
    public ExamScheduleResponse getMyExamSchedule(String username, Long hocKyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
        HocKy hk = resolveHocKy(hocKyId);

        List<DangKyHocPhan> dks = dangKyHocPhanRepository.findRegisteredCoursesInSemester(
                sv.getIdSinhVien(), hk.getIdHocKy());

        List<Long> lopIds = dks.stream()
                .map(d -> d.getLopHocPhan().getIdLopHp())
                .distinct()
                .toList();

        Map<Long, LichThi> lichByLop = lopIds.isEmpty()
                ? Map.of()
                : lichThiRepository.findByLopHocPhan_IdLopHpIn(lopIds).stream()
                        .collect(Collectors.toMap(l -> l.getLopHocPhan().getIdLopHp(), Function.identity(), (a, b) -> a));

        List<Long> dkIds = dks.stream().map(DangKyHocPhan::getIdDangKy).toList();
        Map<Long, PhieuDuThi> phieuByDk = dkIds.isEmpty()
                ? Map.of()
                : phieuDuThiRepository.findByDangKy_IdDangKyIn(dkIds).stream()
                        .collect(Collectors.toMap(p -> p.getDangKy().getIdDangKy(), Function.identity(), (a, b) -> a));

        List<ExamScheduleRowResponse> rows = new ArrayList<>();
        int coLich = 0;
        for (DangKyHocPhan d : dks) {
            LopHocPhan lhp = d.getLopHocPhan();
            HocPhan hp = lhp.getHocPhan();
            LichThi lt = lichByLop.get(lhp.getIdLopHp());
            PhieuDuThi ph = phieuByDk.get(d.getIdDangKy());
            boolean has = lt != null;
            if (has) {
                coLich++;
            }
            rows.add(ExamScheduleRowResponse.builder()
                    .idDangKy(d.getIdDangKy())
                    .idLopHp(lhp.getIdLopHp())
                    .maLopHp(lhp.getMaLopHp())
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .soTinChi(hp != null ? hp.getSoTinChi() : null)
                    .coLichThi(has)
                    .lanThi(lt != null ? lt.getLanThi() : null)
                    .ngayThi(lt != null ? lt.getNgayThi() : null)
                    .caThi(lt != null ? lt.getCaThi() : null)
                    .gioBatDau(lt != null ? lt.getGioBatDau() : null)
                    .phongThi(lt != null ? lt.getPhongThi() : null)
                    .soBaoDanh(ph != null ? ph.getSoBaoDanh() : null)
                    .trangThaiDuThi(ph != null ? ph.getTrangThaiDuThi() : "CHUA_CAP")
                    .lyDo(ph != null ? ph.getLyDo() : null)
                    .build());
        }

        return ExamScheduleResponse.builder()
                .idHocKy(hk.getIdHocKy())
                .hocKyLabel(formatHocKy(hk))
                .tongMonCoDangKy(dks.size())
                .tongMonCoLichThi(coLich)
                .rows(rows)
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
