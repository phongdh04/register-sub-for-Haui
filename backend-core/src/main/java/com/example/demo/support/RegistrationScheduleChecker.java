package com.example.demo.support;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.RegistrationWindow;
import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.repository.RegistrationWindowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Kiểm tra cửa sổ thời gian đăng ký.
 *
 * <p>Thứ tự áp dụng (specific → general):
 * <ol>
 *   <li>Window khớp (hocKy, phase, namNhapHoc, idNganh) trong bảng {@code registration_window}.</li>
 *   <li>Window khớp (hocKy, phase, namNhapHoc) — không quan tâm ngành.</li>
 *   <li>Window khớp (hocKy, phase) — toàn cohort.</li>
 * </ol>
 *
 * <p>Chỉ bảng {@code registration_window} quyết định mở/đóng. Mốc PRE/OFFICAL trên {@link HocKy} không tự
 * bật đăng ký khi chưa có dòng cửa sổ tương ứng trong DB.</p>
 */
@Component
@RequiredArgsConstructor
public class RegistrationScheduleChecker {

    private final RegistrationWindowRepository registrationWindowRepository;



    // -------- API gốc (back-compat: HocKy-only) --------

    public boolean isPreRegistrationOpen(HocKy hk) {
        return isPreRegistrationOpenFor(hk, null, null);
    }

    public boolean isOfficialRegistrationOpen(HocKy hk) {
        return isOfficialRegistrationOpenFor(hk, null, null);
    }

    // -------- API mới: cohort/ngành-aware --------

    /**
     * Pre-registration mở cho 1 sinh vien dua tren cohort + nganh.
     * @param hk         hoc ky muc tieu
     * @param namNhapHoc nam nhap hoc cua sinh vien (cohort, vd 2021)
     * @param nganhId    id nganh sinh vien dang theo
     */
    public boolean isPreRegistrationOpenFor(HocKy hk, Integer namNhapHoc, Long nganhId) {
        return isOpenFor(hk, RegistrationPhase.PRE, namNhapHoc, nganhId);
    }

    public boolean isOfficialRegistrationOpenFor(HocKy hk, Integer namNhapHoc, Long nganhId) {
        return isOpenFor(hk, RegistrationPhase.OFFICIAL, namNhapHoc, nganhId);
    }

    public void requirePreRegistrationOpen(HocKy hk) {
        requirePreRegistrationOpenFor(hk, null, null);
    }

    public void requirePreRegistrationOpenFor(HocKy hk, Integer namNhapHoc, Long nganhId) {
        if (!isPreRegistrationOpenFor(hk, namNhapHoc, nganhId)) {
            throw new IllegalArgumentException(
                    "Chưa đến hoặc đã hết phiên đăng ký trước (giỏ nháp) cho cohort/ngành này.");
        }
    }

    public void requireOfficialRegistrationOpenFor(HocKy hk, Integer namNhapHoc, Long nganhId) {
        if (!isOfficialRegistrationOpenFor(hk, namNhapHoc, nganhId)) {
            throw new IllegalArgumentException(
                    "Chưa đến hoặc đã hết phiên đăng ký chính thức cho cohort/ngành này.");
        }
    }

    // -------- Internal --------

    private boolean isOpenFor(HocKy hk,
                              RegistrationPhase phase,
                              Integer namNhapHoc,
                              Long nganhId) {
        if (hk == null) {
            return false;
        }
        Optional<RegistrationWindow> match = resolveWindow(hk.getIdHocKy(), phase, namNhapHoc, nganhId);
        Instant now = Instant.now();
        if (match.isPresent()) {
            RegistrationWindow w = match.get();
            return !now.isBefore(w.getOpenAt()) && !now.isAfter(w.getCloseAt());
        }
        return false;
    }

    private Optional<RegistrationWindow> resolveWindow(Long hocKyId,
                                                       RegistrationPhase phase,
                                                       Integer namNhapHoc,
                                                       Long nganhId) {
        if (registrationWindowRepository == null || hocKyId == null) {
            return Optional.empty();
        }
        List<RegistrationWindow> hits = registrationWindowRepository.findByHocKyAndPhaseOrdered(hocKyId, phase)
                .stream()
                .filter(w -> scopeMatches(w, namNhapHoc, nganhId))
                .toList();
        return hits.isEmpty() ? Optional.empty() : Optional.of(hits.get(0));
    }

    private static boolean scopeMatches(RegistrationWindow w, Integer studentNamNhapHoc, Long studentNganhId) {
        Integer windowNam = w.getNamNhapHoc();
        Long windowNganhId = w.getNganhDaoTao() != null ? w.getNganhDaoTao().getIdNganh() : null;
        boolean cohortMatch = cohortMatches(windowNam, studentNamNhapHoc);
        boolean nganhMatch = windowNganhId == null || (studentNganhId != null && windowNganhId.equals(studentNganhId));
        return cohortMatch && nganhMatch;
    }

    private static boolean cohortMatches(Integer windowNam, Integer studentNam) {
        if (windowNam == null) {
            return true;
        }
        if (studentNam == null) {
            return false;
        }
        if (windowNam.equals(studentNam)) {
            return true;
        }
        // Compatibility: cho phép map dữ liệu khóa dạng 2 chữ số (vd 17) với năm đầy đủ (2017).
        return windowNam % 100 == studentNam % 100;
    }
}
