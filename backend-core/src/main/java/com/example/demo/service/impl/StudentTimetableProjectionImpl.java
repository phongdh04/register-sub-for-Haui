package com.example.demo.service.impl;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.StudentTimetableEntry;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.StudentTimetableEntryRepository;
import com.example.demo.service.IStudentTimetableProjection;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sprint 5 — Triển khai projection TKB sinh viên.
 *
 * <p>Đọc {@code thoi_khoa_bieu_json} của {@code LopHocPhan} (định dạng:
 * {@code [{"thu":2,"tiet":"1-3","phong":"A.101","ngay_bat_dau":"2024-09-10","ngay_ket_thuc":"2024-12-10"}]})
 * và denormalize thành rows — mỗi slot 1 row trong {@code student_timetable_entry}.
 *
 * <p>Idempotent: trước khi insert luôn xoá hết slot cũ của {@code idDangKy}.
 *
 * <p>Tất cả phương thức ghi đều {@code REQUIRES_NEW} để tách khỏi TX gọi (AFTER_COMMIT
 * hook không có TX active mặc định, nên cần chạy TX riêng).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentTimetableProjectionImpl implements IStudentTimetableProjection {

    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final StudentTimetableEntryRepository entryRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int upsertForRegistration(Long idDangKy) {
        if (idDangKy == null) {
            return 0;
        }
        DangKyHocPhan dkhp = entityManager.find(DangKyHocPhan.class, idDangKy);
        if (dkhp == null) {
            log.warn("⚠️ [TKB-Projection] không tìm thấy DangKyHocPhan id={}", idDangKy);
            return 0;
        }

        entryRepository.deleteByIdDangKy(idDangKy);

        LopHocPhan lhp = dkhp.getLopHocPhan();
        if (lhp == null) {
            log.warn("⚠️ [TKB-Projection] DangKy id={} không có LopHocPhan", idDangKy);
            return 0;
        }
        List<Map<String, Object>> tkb = lhp.getThoiKhoaBieuJson();
        if (tkb == null || tkb.isEmpty()) {
            log.info("ℹ️ [TKB-Projection] Lớp {} chưa có TKB JSON — bỏ qua projection.",
                    lhp.getMaLopHp());
            return 0;
        }

        Long svId = dkhp.getSinhVien() != null ? dkhp.getSinhVien().getIdSinhVien() : null;
        Long hkId = dkhp.getHocKy() != null ? dkhp.getHocKy().getIdHocKy() : null;
        HocPhan hp = lhp.getHocPhan();
        GiangVien gv = lhp.getGiangVien();

        List<StudentTimetableEntry> rows = new ArrayList<>(tkb.size());
        for (int i = 0; i < tkb.size(); i++) {
            Map<String, Object> slot = tkb.get(i);
            rows.add(StudentTimetableEntry.builder()
                    .idSinhVien(svId)
                    .idHocKy(hkId)
                    .idDangKy(idDangKy)
                    .idLopHp(lhp.getIdLopHp())
                    .maLopHp(lhp.getMaLopHp())
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .tenGiangVien(gv != null ? gv.getTenGiangVien() : null)
                    .slotIndex((short) i)
                    .thu(parseShort(slot.get("thu")))
                    .tiet(parseString(slot.get("tiet")))
                    .phong(parseString(slot.get("phong")))
                    .ngayBatDau(parseDate(slot.get("ngay_bat_dau")))
                    .ngayKetThuc(parseDate(slot.get("ngay_ket_thuc")))
                    .build());
        }
        entryRepository.saveAll(rows);
        log.info("✅ [TKB-Projection] upsert idDangKy={} sv={} hk={} slots={}",
                idDangKy, svId, hkId, rows.size());
        return rows.size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int removeForRegistration(Long idDangKy) {
        if (idDangKy == null) {
            return 0;
        }
        int removed = entryRepository.deleteByIdDangKy(idDangKy);
        log.info("🗑️ [TKB-Projection] xoá idDangKy={} slots={}", idDangKy, removed);
        return removed;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int rebuildForStudent(Long sinhVienId, Long hocKyId) {
        if (sinhVienId == null || hocKyId == null) {
            return 0;
        }
        entryRepository.deleteBySinhVienAndHocKy(sinhVienId, hocKyId);
        List<DangKyHocPhan> regs = dangKyHocPhanRepository
                .findRegisteredCoursesInSemester(sinhVienId, hocKyId);
        int total = 0;
        for (DangKyHocPhan d : regs) {
            total += upsertForRegistrationInternal(d);
        }
        log.info("🔁 [TKB-Projection] rebuild sv={} hk={} regs={} slots={}",
                sinhVienId, hocKyId, regs.size(), total);
        return total;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<StudentTimetableEntry> readForStudent(Long sinhVienId, Long hocKyId) {
        if (sinhVienId == null || hocKyId == null) {
            return List.of();
        }
        return entryRepository.findBySinhVienAndHocKy(sinhVienId, hocKyId);
    }

    /** Nhánh internal cho rebuild — dùng entity đã load thay vì find lại (tránh N+1). */
    private int upsertForRegistrationInternal(DangKyHocPhan dkhp) {
        LopHocPhan lhp = dkhp.getLopHocPhan();
        if (lhp == null || lhp.getThoiKhoaBieuJson() == null || lhp.getThoiKhoaBieuJson().isEmpty()) {
            return 0;
        }
        List<Map<String, Object>> tkb = lhp.getThoiKhoaBieuJson();
        Long svId = dkhp.getSinhVien() != null ? dkhp.getSinhVien().getIdSinhVien() : null;
        Long hkId = dkhp.getHocKy() != null ? dkhp.getHocKy().getIdHocKy() : null;
        HocPhan hp = lhp.getHocPhan();
        GiangVien gv = lhp.getGiangVien();
        List<StudentTimetableEntry> rows = new ArrayList<>(tkb.size());
        for (int i = 0; i < tkb.size(); i++) {
            Map<String, Object> slot = tkb.get(i);
            rows.add(StudentTimetableEntry.builder()
                    .idSinhVien(svId)
                    .idHocKy(hkId)
                    .idDangKy(dkhp.getIdDangKy())
                    .idLopHp(lhp.getIdLopHp())
                    .maLopHp(lhp.getMaLopHp())
                    .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                    .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                    .tenGiangVien(gv != null ? gv.getTenGiangVien() : null)
                    .slotIndex((short) i)
                    .thu(parseShort(slot.get("thu")))
                    .tiet(parseString(slot.get("tiet")))
                    .phong(parseString(slot.get("phong")))
                    .ngayBatDau(parseDate(slot.get("ngay_bat_dau")))
                    .ngayKetThuc(parseDate(slot.get("ngay_ket_thuc")))
                    .build());
        }
        entryRepository.saveAll(rows);
        return rows.size();
    }

    private static Short parseShort(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.shortValue();
        try {
            return Short.parseShort(v.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String parseString(Object v) {
        return v == null ? null : v.toString();
    }

    private static LocalDate parseDate(Object v) {
        if (v == null) return null;
        try {
            return LocalDate.parse(v.toString());
        } catch (Exception ex) {
            return null;
        }
    }
}
