package com.example.demo.service;

import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.repository.ChuongTrinhDaoTaoRepository;
import com.example.demo.repository.CtdtHocPhanRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Giới hạn khóa học/môn học theo CTĐT mới nhất của ngành sinh viên (bảng {@code CTDT_Hoc_Phan}).
 *
 * <p>Dùng cho tìm lớp đăng ký và kiểm tra POST để sinh viên không thấy/ghi các học phần ngoài khung CTĐT.
 */
@Service
@RequiredArgsConstructor
public class StudentCurriculumCourseService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;
    private final PreRegistrationIntentRepository preRegistrationIntentRepository;

    /**
     * Học phần chỉ trong CTĐT ngành (không kèm intent).
     *
     * <p>Xem {@link #listAllowedHocPhanIdsForSearch(String, Long)} cho giao diện tìm lớp.
     */
    @Transactional(readOnly = true)
    public List<Long> listAllowedHocPhanIds(String username) {
        return listCurriculumHocPhanIds(resolveSinhVien(username));
    }

    /**
     * Union CTĐT + học phần đã ghi nhận ở đăng ký dự kiến trong {@code idHocKy}, để pha sau vẫn thấy lớp của intent
     * dù CTĐT seed thiếu hoặc lệch dữ liệu.
     */
    @Transactional(readOnly = true)
    public List<Long> listAllowedHocPhanIdsForSearch(String username, Long idHocKy) {
        SinhVien sv = resolveSinhVien(username);
        LinkedHashSet<Long> merged = new LinkedHashSet<>(listCurriculumHocPhanIds(sv));
        if (idHocKy != null) {
            merged.addAll(preRegistrationIntentRepository.findDistinctHocPhanIdsBySinhVienAndHocKy(
                    sv.getIdSinhVien(), idHocKy));
        }
        return new ArrayList<>(merged);
    }

    /** CTĐT: danh sách học phần thuộc chương trình áp ngành lớp hành chính của sinh viên. */
    private List<Long> listCurriculumHocPhanIds(SinhVien sv) {
        if (sv.getLop() == null || sv.getLop().getNganhDaoTao() == null) {
            return List.of();
        }
        Long nganhId = sv.getLop().getNganhDaoTao().getIdNganh();
        return chuongTrinhDaoTaoRepository
                .findLatestByNganh(nganhId)
                .map(ctdt -> ctdtHocPhanRepository.findAllByCtdtId(ctdt.getIdCtdt()).stream()
                        .map(m -> m.getHocPhan().getIdHocPhan())
                        .distinct()
                        .toList())
                .orElse(List.of());
    }

    /**
     * Cho phép ghi nhận đăng ký học phần nếu trong CTĐT hoặc sinh viên có nguyện vọng PRE học phần đó trong học kỳ.
     */
    @Transactional(readOnly = true)
    public boolean isHocPhanAllowedForEnrollment(SinhVien sv, Long idHocKy, Long idHocPhan) {
        if (sv == null || idHocPhan == null) {
            return false;
        }
        if (isHocPhanInProgram(sv, idHocPhan)) {
            return true;
        }
        if (idHocKy == null) {
            return false;
        }
        return preRegistrationIntentRepository
                .findBySinhVien_IdSinhVienAndHocKy_IdHocKyAndHocPhan_IdHocPhan(
                        sv.getIdSinhVien(), idHocKy, idHocPhan)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isHocPhanInProgram(SinhVien sv, Long idHocPhan) {
        if (sv == null || idHocPhan == null || sv.getLop() == null || sv.getLop().getNganhDaoTao() == null) {
            return false;
        }
        return chuongTrinhDaoTaoRepository
                .findLatestByNganh(sv.getLop().getNganhDaoTao().getIdNganh())
                .map(ctdt -> ctdtHocPhanRepository.existsByChuongTrinhDaoTao_IdCtdtAndHocPhan_IdHocPhan(
                        ctdt.getIdCtdt(), idHocPhan))
                .orElse(false);
    }

    private SinhVien resolveSinhVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay tai khoan: " + username));
        return sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tai khoan chua lien ket ho so sinh vien."));
    }
}
