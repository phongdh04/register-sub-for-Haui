package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.Khoa;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.AtRiskStudentListResponse;
import com.example.demo.payload.response.AtRiskStudentRowResponse;
import com.example.demo.repository.BangDiemMonRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ILecturerAdvisoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerAdvisoryServiceImpl implements ILecturerAdvisoryService {

    private final UserRepository userRepository;
    private final GiangVienRepository giangVienRepository;
    private final BangDiemMonRepository bangDiemMonRepository;

    @Override
    @Transactional(readOnly = true)
    public AtRiskStudentListResponse listAtRiskStudents(String username, Integer minFailTc) {
        int threshold = minFailTc == null ? 12 : minFailTc;
        if (threshold < 1 || threshold > 120) {
            throw new IllegalArgumentException("Ngưỡng tín chỉ rớt phải từ 1 đến 120.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        GiangVien gv = giangVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ giảng viên."));
        Khoa khoa = gv.getKhoa();
        if (khoa == null) {
            throw new IllegalArgumentException("Giảng viên chưa được gán khoa — không thể lọc SV theo khoa.");
        }

        List<Object[]> raw = bangDiemMonRepository.findStudentsWithFailedCreditsAboveThreshold(
                khoa.getIdKhoa(), threshold);

        List<AtRiskStudentRowResponse> rows = raw.stream()
                .map(r -> AtRiskStudentRowResponse.builder()
                        .idSinhVien((Long) r[0])
                        .maSinhVien((String) r[1])
                        .hoTen((String) r[2])
                        .maLop((String) r[3])
                        .tongTinChiRot(r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO)
                        .soMonRot(r[5] != null ? ((Number) r[5]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());

        return AtRiskStudentListResponse.builder()
                .idKhoa(khoa.getIdKhoa())
                .tenKhoa(khoa.getTenKhoa())
                .nguongTinChiRot(threshold)
                .tongSoBanGhi(rows.size())
                .rows(rows)
                .build();
    }
}
