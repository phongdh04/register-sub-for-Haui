package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.PreRegistrationIntent;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.PreRegistrationIntentSubmitRequest;
import com.example.demo.payload.response.PreRegistrationIntentResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IPreRegistrationIntentService;
import com.example.demo.service.StudentCurriculumCourseService;
import com.example.demo.support.RegistrationScheduleChecker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Sinh viên thao tác nguyện vọng đăng ký dự kiến.
 *
 * <p>Quy tắc:
 * <ul>
 *   <li>Chỉ chấp nhận khi pha PRE đang mở cho cohort/ngành của sinh viên.</li>
 *   <li>1 sinh viên không tạo trùng intent (sv, hocKy, hocPhan); muốn đổi thì update.</li>
 *   <li>Sinh viên chỉ có quyền sửa/xóa intent của chính mình.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PreRegistrationIntentServiceImpl implements IPreRegistrationIntentService {

    private final PreRegistrationIntentRepository intentRepository;
    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final HocPhanRepository hocPhanRepository;
    private final RegistrationScheduleChecker registrationScheduleChecker;
    private final StudentCurriculumCourseService studentCurriculumCourseService;

    @Override
    @Transactional
    public PreRegistrationIntentResponse submit(String username, PreRegistrationIntentSubmitRequest req) {
        SinhVien sv = resolveSinhVien(username);
        HocKy hk = loadHocKy(req.getIdHocKy());
        HocPhan hp = loadHocPhan(req.getIdHocPhan());
        ensurePrePhaseOpen(sv, hk);
        ensureHocPhanInStudentCurriculum(sv, hp.getIdHocPhan());

        intentRepository
                .findBySinhVien_IdSinhVienAndHocKy_IdHocKyAndHocPhan_IdHocPhan(
                        sv.getIdSinhVien(), hk.getIdHocKy(), hp.getIdHocPhan())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Bạn đã có nguyện vọng cho học phần này trong học kỳ này. Hãy cập nhật thay vì tạo mới.");
                });

        PreRegistrationIntent intent = PreRegistrationIntent.builder()
                .sinhVien(sv)
                .hocKy(hk)
                .hocPhan(hp)
                .priority(req.getPriority() != null ? req.getPriority() : 1)
                .ghiChu(req.getGhiChu())
                .build();
        return toResponse(intentRepository.save(intent));
    }

    @Override
    @Transactional
    public PreRegistrationIntentResponse update(String username, Long intentId,
                                                PreRegistrationIntentSubmitRequest req) {
        SinhVien sv = resolveSinhVien(username);
        PreRegistrationIntent intent = intentRepository.findById(intentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyện vọng: " + intentId));
        if (!intent.getSinhVien().getIdSinhVien().equals(sv.getIdSinhVien())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền sửa nguyện vọng này.");
        }
        // Cho phép đổi học phần / học kỳ → coi như đổi sang slot mới, vẫn check trùng + window.
        HocKy hk = loadHocKy(req.getIdHocKy());
        HocPhan hp = loadHocPhan(req.getIdHocPhan());
        ensurePrePhaseOpen(sv, hk);
        ensureHocPhanInStudentCurriculum(sv, hp.getIdHocPhan());

        boolean scopeChanged = !hk.getIdHocKy().equals(intent.getHocKy().getIdHocKy())
                || !hp.getIdHocPhan().equals(intent.getHocPhan().getIdHocPhan());
        if (scopeChanged) {
            intentRepository
                    .findBySinhVien_IdSinhVienAndHocKy_IdHocKyAndHocPhan_IdHocPhan(
                            sv.getIdSinhVien(), hk.getIdHocKy(), hp.getIdHocPhan())
                    .ifPresent(other -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Đã có nguyện vọng khác cho học phần/học kỳ này.");
                    });
        }

        intent.setHocKy(hk);
        intent.setHocPhan(hp);
        intent.setPriority(req.getPriority() != null ? req.getPriority() : intent.getPriority());
        intent.setGhiChu(req.getGhiChu());
        return toResponse(intentRepository.save(intent));
    }

    @Override
    @Transactional
    public void delete(String username, Long intentId) {
        SinhVien sv = resolveSinhVien(username);
        PreRegistrationIntent intent = intentRepository.findById(intentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyện vọng: " + intentId));
        if (!intent.getSinhVien().getIdSinhVien().equals(sv.getIdSinhVien())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa nguyện vọng này.");
        }
        intentRepository.delete(intent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreRegistrationIntentResponse> listMine(String username, Long idHocKy) {
        SinhVien sv = resolveSinhVien(username);
        Long resolvedHocKy = idHocKy != null
                ? idHocKy
                : hocKyRepository.findByTrangThaiHienHanhTrue()
                        .map(HocKy::getIdHocKy)
                        .orElse(null);
        if (resolvedHocKy == null) {
            return List.of();
        }
        return intentRepository.findBySinhVienAndHocKy(sv.getIdSinhVien(), resolvedHocKy)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -- Helpers --

    private SinhVien resolveSinhVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        return sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
    }

    private HocKy loadHocKy(Long id) {
        return hocKyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + id));
    }

    private HocPhan loadHocPhan(Long id) {
        return hocPhanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học phần: " + id));
    }

    private void ensurePrePhaseOpen(SinhVien sv, HocKy hk) {
        Integer namNhapHoc = sv.getLop() != null ? sv.getLop().getNamNhapHoc() : null;
        Long idNganh = sv.getLop() != null && sv.getLop().getNganhDaoTao() != null
                ? sv.getLop().getNganhDaoTao().getIdNganh()
                : null;
        if (!registrationScheduleChecker.isPreRegistrationOpenFor(hk, namNhapHoc, idNganh)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Pha đăng ký dự kiến chưa mở (hoặc đã đóng) cho khóa/ngành của bạn.");
        }
    }

    private void ensureHocPhanInStudentCurriculum(SinhVien sv, Long idHocPhan) {
        if (!studentCurriculumCourseService.isHocPhanInProgram(sv, idHocPhan)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Học phần không thuộc chương trình đào tạo (CTĐT) của ngành bạn.");
        }
    }

    private PreRegistrationIntentResponse toResponse(PreRegistrationIntent i) {
        HocKy hk = i.getHocKy();
        HocPhan hp = i.getHocPhan();
        return PreRegistrationIntentResponse.builder()
                .id(i.getId())
                .idSinhVien(i.getSinhVien().getIdSinhVien())
                .idHocKy(hk.getIdHocKy())
                .tenHocKy(hk != null ? "HK" + hk.getKyThu() + " " + hk.getNamHoc() : null)
                .idHocPhan(hp.getIdHocPhan())
                .maHocPhan(hp.getMaHocPhan())
                .tenHocPhan(hp.getTenHocPhan())
                .soTinChi(hp.getSoTinChi())
                .priority(i.getPriority())
                .ghiChu(i.getGhiChu())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
