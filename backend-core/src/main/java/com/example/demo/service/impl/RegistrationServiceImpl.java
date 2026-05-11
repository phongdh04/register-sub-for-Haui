package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.payload.response.RegistrationStudentResponse;
import com.example.demo.payload.response.RegistrationWindowStatusResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IRegistrationService;
import com.example.demo.service.StudentCurriculumCourseService;
import com.example.demo.service.validation.IRegistrationValidationHandler;
import com.example.demo.service.validation.RegistrationValidationException;
import com.example.demo.service.validation.handler.DuplicateRegistrationHandler;
import com.example.demo.service.validation.handler.PrerequisiteCourseHandler;
import com.example.demo.service.validation.handler.ScheduleConflictHandler;
import com.example.demo.support.RegistrationScheduleChecker;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Sinh vien dang ky chinh thuc qua REST (sync, khong qua Kafka).
 *
 * Tai su dung validation chain co san: TrungLop -> TrungLich -> TienQuyet.
 * Atomic increment si so giong path Kafka de tranh race condition.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements IRegistrationService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final PreRegistrationIntentRepository preRegistrationIntentRepository;
    private final StudentCurriculumCourseService studentCurriculumCourseService;

    private final DuplicateRegistrationHandler duplicateRegistrationHandler;
    private final ScheduleConflictHandler scheduleConflictHandler;
    private final PrerequisiteCourseHandler prerequisiteCourseHandler;
    private final RegistrationScheduleChecker registrationScheduleChecker;

    private IRegistrationValidationHandler validationChain;

    @PostConstruct
    void buildChain() {
        duplicateRegistrationHandler
                .setNext(scheduleConflictHandler)
                .setNext(prerequisiteCourseHandler);
        validationChain = duplicateRegistrationHandler;
    }

    @Override
    @Transactional
    public RegistrationStudentResponse register(String username, Long idLopHp, Long idHocKy) {
        SinhVien sv = resolveSinhVien(username);
        HocKy hk = resolveHocKy(idHocKy);
        String activePhase = ensureRegistrationOpen(sv, hk);

        LopHocPhan lhp = lopHocPhanRepository.findById(idLopHp)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay lop hoc phan: " + idLopHp));
        if (lhp.getHocKy() == null || !lhp.getHocKy().getIdHocKy().equals(hk.getIdHocKy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lop hoc phan khong thuoc hoc ky dang chon.");
        }
        if (lhp.getHocPhan() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lop hoc phan khong lien ket hoc phan.");
        }
        if (!studentCurriculumCourseService.isHocPhanAllowedForEnrollment(
                sv, hk.getIdHocKy(), lhp.getHocPhan().getIdHocPhan())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Hoc phan khong nam trong CTDT nganh hoac khong co trong dang ky du kien (PRE) cua hoc ky nay.");
        }

        Integer cohortNam = resolveNamNhapHoc(sv);
        Long idNganhGate = resolveNganhId(sv);
        boolean preOpenGate = registrationScheduleChecker.isPreRegistrationOpenFor(hk, cohortNam, idNganhGate);
        boolean officialOpenGate = registrationScheduleChecker.isOfficialRegistrationOpenFor(hk, cohortNam, idNganhGate);
        boolean officialOnlyPhase = officialOpenGate && !preOpenGate;
        if (officialOnlyPhase) {
            boolean hasIntent = preRegistrationIntentRepository
                    .findBySinhVien_IdSinhVienAndHocKy_IdHocKyAndHocPhan_IdHocPhan(
                            sv.getIdSinhVien(), hk.getIdHocKy(), lhp.getHocPhan().getIdHocPhan())
                    .isPresent();
            if (!hasIntent) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Dang ky chinh thuc chi duoc cho cac hoc phan da nam trong dang ky du kien (pha PRE).");
            }
        }

        RegistrationMessageDto msg = RegistrationMessageDto.builder()
                .idSinhVien(sv.getIdSinhVien())
                .idLopHp(idLopHp)
                .idHocKy(hk.getIdHocKy())
                .traceId("HTTP-" + sv.getIdSinhVien() + "-" + idLopHp + "-" + System.currentTimeMillis())
                .build();
        try {
            if ("PRE".equals(activePhase)) {
                duplicateRegistrationHandler.validateSelfOnly(msg);
                scheduleConflictHandler.validateSelfOnly(msg);
            } else {
                validationChain.validate(msg);
            }
        } catch (RegistrationValidationException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex);
        }

        int updated = lopHocPhanRepository.incrementSiSoThucTe(lhp.getIdLopHp());
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lop " + lhp.getMaLopHp() + " da het cho.");
        }

        DangKyHocPhan dkhp = DangKyHocPhan.builder()
                .sinhVien(sv)
                .lopHocPhan(lhp)
                .hocKy(hk)
                .trangThaiDangKy("THANH_CONG")
                .build();
        dangKyHocPhanRepository.save(dkhp);
        log.info("DKHP-HTTP svId={} lhp={} idDangKy={}", sv.getIdSinhVien(), lhp.getMaLopHp(), dkhp.getIdDangKy());

        return buildSnapshot(sv, hk);
    }

    @Override
    @Transactional
    public void cancel(String username, Long idDangKy) {
        SinhVien sv = resolveSinhVien(username);
        DangKyHocPhan d = dangKyHocPhanRepository.findById(idDangKy)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay dang ky: " + idDangKy));
        if (d.getSinhVien() == null || !d.getSinhVien().getIdSinhVien().equals(sv.getIdSinhVien())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Khong co quyen huy dang ky cua nguoi khac.");
        }
        if (!"THANH_CONG".equals(d.getTrangThaiDangKy()) && !"CHO_DUYET".equals(d.getTrangThaiDangKy())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Dang ky dang o trang thai " + d.getTrangThaiDangKy() + ", khong huy duoc.");
        }

        d.setTrangThaiDangKy("RUT_MON");
        dangKyHocPhanRepository.save(d);
        if (d.getLopHocPhan() != null) {
            lopHocPhanRepository.decrementSiSoThucTe(d.getLopHocPhan().getIdLopHp());
        }
        log.info("CANCEL-HTTP svId={} idDangKy={}", sv.getIdSinhVien(), idDangKy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationStudentResponse.RegisteredItem> listMine(String username, Long idHocKy) {
        SinhVien sv = resolveSinhVien(username);
        HocKy hk = resolveHocKy(idHocKy);
        return mapItems(sv, hk);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationWindowStatusResponse getMyWindowStatus(String username, Long idHocKy) {
        SinhVien sv = resolveSinhVien(username);
        HocKy hk = resolveHocKy(idHocKy);
        Integer namNhapHoc = resolveNamNhapHoc(sv);
        Long idNganh = resolveNganhId(sv);
        NganhDaoTao nganh = sv.getLop() != null ? sv.getLop().getNganhDaoTao() : null;

        boolean officialOpen = registrationScheduleChecker.isOfficialRegistrationOpenFor(hk, namNhapHoc, idNganh);
        boolean preOpen = registrationScheduleChecker.isPreRegistrationOpenFor(hk, namNhapHoc, idNganh);

        String activePhase;
        if (preOpen && officialOpen) {
            activePhase = "PRE_AND_OFFICIAL";
        } else if (officialOpen) {
            activePhase = "OFFICIAL";
        } else if (preOpen) {
            activePhase = "PRE";
        } else {
            activePhase = "NONE";
        }

        String debugReason = null;
        if (!preOpen && !officialOpen) {
            StringBuilder b = new StringBuilder();
            b.append("Chua co window dang mo cho sinh vien (");
            b.append("cohort=").append(namNhapHoc != null ? "K" + namNhapHoc : "(thieu)");
            b.append(", nganh=").append(nganh != null ? nganh.getTenNganh() : "(thieu)");
            b.append("). ");
            if (idNganh == null) {
                b.append("Goi y: tai khoan SV chua duoc gan vao Lop/Nganh — admin can update seed.");
            } else {
                b.append("Goi y: kiem tra admin co tao window cho dung cohort+nganh nay khong.");
            }
            debugReason = b.toString();
        }

        Lop lop = sv.getLop();
        return RegistrationWindowStatusResponse.builder()
                .idHocKy(hk.getIdHocKy())
                .tenHocKy("HK" + hk.getKyThu() + " " + hk.getNamHoc())
                .maSinhVien(sv.getMaSinhVien())
                .hoTenSinhVien(sv.getHoTen())
                .tenLop(lop != null ? lop.getTenLop() : null)
                .namNhapHoc(namNhapHoc)
                .idNganh(idNganh)
                .tenNganh(nganh != null ? nganh.getTenNganh() : null)
                .dangKyChinhThucDangMo(officialOpen)
                .officialOpenAt(hk.getDangKyChinhThucTu())
                .officialCloseAt(hk.getDangKyChinhThucDen())
                .officialScopeNote(buildScopeNote(officialOpen, namNhapHoc, nganh, "chinh thuc"))
                .preDangKyDangMo(preOpen)
                .preOpenAt(hk.getPreDangKyMoTu())
                .preCloseAt(hk.getPreDangKyMoDen())
                .preScopeNote(buildScopeNote(preOpen, namNhapHoc, nganh, "du kien"))
                .dangKyAnyDangMo(preOpen || officialOpen)
                .activePhase(activePhase)
                .debugReason(debugReason)
                .build();
    }

    /**
     * Cho phep dang ky neu PRE hoac OFFICIAL dang mo.
     *
     * @return ten phase active de caller quyet dinh chay full chain hay rut gon.
     */
    private String ensureRegistrationOpen(SinhVien sv, HocKy hk) {
        Integer namNhapHoc = resolveNamNhapHoc(sv);
        Long idNganh = resolveNganhId(sv);
        boolean officialOpen = registrationScheduleChecker.isOfficialRegistrationOpenFor(hk, namNhapHoc, idNganh);
        boolean preOpen = registrationScheduleChecker.isPreRegistrationOpenFor(hk, namNhapHoc, idNganh);
        if (officialOpen) {
            return "OFFICIAL";
        }
        if (preOpen) {
            return "PRE";
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Phien dang ky chua mo (hoac da dong) cho khoa/nganh cua ban. "
                + "Cohort=" + (namNhapHoc != null ? "K" + namNhapHoc : "(thieu)")
                + ", nganh=" + (idNganh != null ? "id" + idNganh : "(thieu)") + ".");
    }

    private SinhVien resolveSinhVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay tai khoan: " + username));
        return sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tai khoan chua lien ket ho so sinh vien."));
    }

    private HocKy resolveHocKy(Long idHocKy) {
        if (idHocKy != null) {
            return hocKyRepository.findById(idHocKy)
                    .orElseThrow(() -> new EntityNotFoundException("Khong tim thay hoc ky: " + idHocKy));
        }
        return hocKyRepository.findByTrangThaiHienHanhTrue()
                .orElseGet(() -> hocKyRepository.findTopByOrderByIdHocKyDesc()
                        .orElseThrow(() -> new EntityNotFoundException("Chua cau hinh hoc ky.")));
    }

    private static Integer resolveNamNhapHoc(SinhVien sv) {
        Lop lop = sv != null ? sv.getLop() : null;
        return lop != null ? lop.getNamNhapHoc() : null;
    }

    private static Long resolveNganhId(SinhVien sv) {
        if (sv == null || sv.getLop() == null) {
            return null;
        }
        NganhDaoTao n = sv.getLop().getNganhDaoTao();
        return n != null ? n.getIdNganh() : null;
    }

    private RegistrationStudentResponse buildSnapshot(SinhVien sv, HocKy hk) {
        List<RegistrationStudentResponse.RegisteredItem> items = mapItems(sv, hk);
        int totalCredits = items.stream()
                .mapToInt(it -> it.getSoTinChi() != null ? it.getSoTinChi() : 0)
                .sum();
        BigDecimal totalPhi = items.stream()
                .map(it -> it.getHocPhi() != null ? it.getHocPhi() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return RegistrationStudentResponse.builder()
                .idHocKy(hk.getIdHocKy())
                .tenHocKy("HK" + hk.getKyThu() + " " + hk.getNamHoc())
                .tongSoMon(items.size())
                .tongTinChi(totalCredits)
                .tongHocPhi(totalPhi)
                .items(items)
                .build();
    }

    private List<RegistrationStudentResponse.RegisteredItem> mapItems(SinhVien sv, HocKy hk) {
        List<DangKyHocPhan> rows = dangKyHocPhanRepository
                .findRegisteredCoursesInSemester(sv.getIdSinhVien(), hk.getIdHocKy());
        List<RegistrationStudentResponse.RegisteredItem> out = new ArrayList<>(rows.size());
        for (DangKyHocPhan d : rows) {
            LopHocPhan lhp = d.getLopHocPhan();
            HocPhan hp = lhp != null ? lhp.getHocPhan() : null;
            GiangVien gv = lhp != null ? lhp.getGiangVien() : null;
            out.add(RegistrationStudentResponse.RegisteredItem.builder()
                    .idDangKy(d.getIdDangKy())
                    .idLopHp(lhp != null ? lhp.getIdLopHp() : null)
                    .maLopHp(lhp != null ? lhp.getMaLopHp() : null)
                    .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .soTinChi(hp != null ? hp.getSoTinChi() : null)
                    .tenGiangVien(gv != null ? gv.getTenGiangVien() : null)
                    .siSoToiDa(lhp != null ? lhp.getSiSoToiDa() : null)
                    .siSoThucTe(lhp != null ? lhp.getSiSoThucTe() : null)
                    .hocPhi(lhp != null ? lhp.getHocPhi() : null)
                    .trangThaiDangKy(d.getTrangThaiDangKy())
                    .ngayDangKy(d.getNgayDangKy())
                    .build());
        }
        return out;
    }

    private static String buildScopeNote(boolean mo, Integer namNhapHoc,
            NganhDaoTao nganh, String label) {
        if (!mo) {
            return "Khong trong phien mo " + label + " hoac cohort/nganh khong khop.";
        }
        StringBuilder sb = new StringBuilder("Dang trong phien ");
        sb.append(label);
        if (namNhapHoc != null) {
            sb.append(" — khoa K").append(namNhapHoc);
        }
        if (nganh != null) {
            sb.append(" — nganh ").append(nganh.getTenNganh());
        }
        sb.append('.');
        return sb.toString();
    }
}
