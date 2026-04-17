package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.AdvisoryAtRiskListResponse;
import com.example.demo.payload.response.AdvisoryAtRiskStudentResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ILecturerAdvisoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LecturerAdvisoryServiceImpl implements ILecturerAdvisoryService {

    private final UserRepository userRepository;
    private final GiangVienRepository giangVienRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public AdvisoryAtRiskListResponse listAtRiskStudents(String lecturerUsername, int minFailedCredits) {
        if (minFailedCredits < 0) {
            minFailedCredits = 0;
        }
        User user = userRepository.findByUsername(lecturerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + lecturerUsername));
        GiangVien gv = giangVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ giảng viên."));

        if (gv.getKhoa() == null) {
            return AdvisoryAtRiskListResponse.builder()
                    .khoaMa(null)
                    .khoaTen(null)
                    .minFailedCredits(minFailedCredits)
                    .tongSoSinhVien(0)
                    .hint("Hồ sơ giảng viên chưa gán khoa — không xác định được phạm vi sinh viên.")
                    .sinhViens(List.of())
                    .build();
        }

        Long idKhoa = gv.getKhoa().getIdKhoa();
        List<Object[]> rows = dangKyHocPhanRepository.findStudentsWithFailedCreditsAboveThreshold(idKhoa, minFailedCredits);
        if (rows.isEmpty()) {
            return AdvisoryAtRiskListResponse.builder()
                    .khoaMa(gv.getKhoa().getMaKhoa())
                    .khoaTen(gv.getKhoa().getTenKhoa())
                    .minFailedCredits(minFailedCredits)
                    .tongSoSinhVien(0)
                    .hint(null)
                    .sinhViens(List.of())
                    .build();
        }

        List<Long> ids = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ids.add((Long) r[0]);
        }
        Map<Long, BigDecimal> gpaBySv = buildGpaMap(ids);

        List<AdvisoryAtRiskStudentResponse> items = new ArrayList<>();
        for (Object[] r : rows) {
            Long idSv = (Long) r[0];
            Number sumTc = (Number) r[5];
            Number cnt = (Number) r[6];
            int tinRot = sumTc != null ? sumTc.intValue() : 0;
            long monRot = cnt != null ? cnt.longValue() : 0L;
            items.add(AdvisoryAtRiskStudentResponse.builder()
                    .idSinhVien(idSv)
                    .maSinhVien((String) r[1])
                    .hoTen((String) r[2])
                    .maLop((String) r[3])
                    .tenLop((String) r[4])
                    .tinChiRot(tinRot)
                    .soMonRot(monRot)
                    .gpaTichLuy(gpaBySv.get(idSv))
                    .build());
        }

        return AdvisoryAtRiskListResponse.builder()
                .khoaMa(gv.getKhoa().getMaKhoa())
                .khoaTen(gv.getKhoa().getTenKhoa())
                .minFailedCredits(minFailedCredits)
                .tongSoSinhVien(items.size())
                .hint(null)
                .sinhViens(items)
                .build();
    }

    private Map<Long, BigDecimal> buildGpaMap(List<Long> svIds) {
        Map<Long, BigDecimal> out = new HashMap<>();
        if (svIds.isEmpty()) {
            return out;
        }
        List<Object[]> gpaRows = dangKyHocPhanRepository.findCumulativeGpaFactorsBySinhVienIds(svIds);
        for (Object[] g : gpaRows) {
            Long id = (Long) g[0];
            BigDecimal sumWx = g[1] != null ? (BigDecimal) g[1] : null;
            Number sumC = (Number) g[2];
            if (sumWx == null || sumC == null || sumC.intValue() == 0) {
                continue;
            }
            BigDecimal gpa = sumWx.divide(BigDecimal.valueOf(sumC.longValue()), 2, RoundingMode.HALF_UP);
            out.put(id, gpa);
        }
        return out;
    }
}
