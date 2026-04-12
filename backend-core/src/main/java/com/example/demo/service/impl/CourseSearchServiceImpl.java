package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.CourseSearchRequest;
import com.example.demo.payload.response.CourseSearchResponse;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.specification.CourseSearchSpecification;
import com.example.demo.service.ICourseSearchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation tìm kiếm môn học (Task 6).
 *
 * Luồng xử lý:
 *   Client request → CourseSearchRequest → buildFrom() Specification
 *     → findAll(spec, pageable) → Map LopHocPhan → CourseSearchResponse
 *
 * SRP: Chỉ xử lý luồng tìm kiếm + mapping, không chứa điều kiện WHERE (ở Specification).
 * OCP: Thêm field response mới → sửa toResponse(), không đụng logic query.
 * DIP: Phụ thuộc ICourseSearchService, không phụ thuộc class cụ thể.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Tất cả query trong service này đều là READ - tối ưu DB pool
public class CourseSearchServiceImpl implements ICourseSearchService {

    private final LopHocPhanRepository lopHocPhanRepository;

    /**
     * Tìm kiếm phân trang với dynamic filter từ request.
     * Bước 1: Build Specification (WHERE clause) từ request.
     * Bước 2: Build Pageable (ORDER BY + LIMIT OFFSET).
     * Bước 3: Gọi repository.findAll(spec, pageable).
     * Bước 4: Map từng LopHocPhan → CourseSearchResponse.
     */
    @Override
    public Page<CourseSearchResponse> searchCourses(CourseSearchRequest request) {
        log.debug("🔍 Tìm kiếm môn: keyword='{}' hocKy={} chiConCho={}",
                request.getKeyword(), request.getIdHocKy(), request.isChiConCho());

        // ── Bước 1: Build dynamic WHERE clause (Specification Pattern) ──
        Specification<LopHocPhan> spec = CourseSearchSpecification.buildFrom(request);

        // ── Bước 2: Build Pageable (sắp xếp an toàn với whitelist) ──────
        Sort sort = buildSort(request.getSortBy(), request.getSortDir());
        Pageable pageable = PageRequest.of(
                Math.max(0, request.getPage()),
                Math.min(100, Math.max(1, request.getSize())), // Giới hạn max 100 item/trang
                sort);

        // ── Bước 3: Query DB ─────────────────────────────────────────────
        Page<LopHocPhan> page = lopHocPhanRepository.findAll(spec, pageable);

        log.debug("✅ Kết quả tìm kiếm: {} lớp / {} trang",
                page.getTotalElements(), page.getTotalPages());

        // ── Bước 4: Map → Response ───────────────────────────────────────
        return page.map(this::toResponse);
    }

    /**
     * Lấy chi tiết một lớp - SV click xem trước khi bấm Đăng ký.
     */
    @Override
    public CourseSearchResponse getCourseDetail(Long idLopHp) {
        LopHocPhan lop = lopHocPhanRepository.findById(idLopHp)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy lớp học phần với ID: " + idLopHp));
        return toResponse(lop);
    }

    // ════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════════

    /**
     * Map LopHocPhan entity → CourseSearchResponse.
     * Tính toán các computed field: siSoConLai, phanTramDay.
     */
    private CourseSearchResponse toResponse(LopHocPhan lop) {
        int thucTe = lop.getSiSoThucTe() != null ? lop.getSiSoThucTe() : 0;
        int toiDa  = lop.getSiSoToiDa() != null  ? lop.getSiSoToiDa()  : 0;
        int conLai = Math.max(0, toiDa - thucTe);
        double phanTram = toiDa > 0 ? (thucTe * 100.0 / toiDa) : 0.0;

        GiangVien gv = lop.getGiangVien();

        return CourseSearchResponse.builder()
                // Lớp học phần
                .idLopHp(lop.getIdLopHp())
                .maLopHp(lop.getMaLopHp())
                .trangThai(lop.getTrangThai())
                .hocPhi(lop.getHocPhi())
                .thoiKhoaBieuJson(lop.getThoiKhoaBieuJson())
                // Học phần (môn)
                .idHocPhan(lop.getHocPhan().getIdHocPhan())
                .maHocPhan(lop.getHocPhan().getMaHocPhan())
                .tenHocPhan(lop.getHocPhan().getTenHocPhan())
                .maIn(lop.getHocPhan().getMaIn())
                .soTinChi(lop.getHocPhan().getSoTinChi())
                .loaiMon(lop.getHocPhan().getLoaiMon())
                .dieuKienRangBuocJson(lop.getHocPhan().getDieuKienRangBuocJson())
                // Học kỳ
                .idHocKy(lop.getHocKy().getIdHocKy())
                .tenHocKy(lop.getHocKy().getTenHocKy())
                // Giảng viên (nullable)
                .idGiangVien(gv != null ? gv.getIdGiangVien() : null)
                .tenGiangVien(gv != null ? gv.getTenGiangVien() : null)
                .emailGiangVien(gv != null ? gv.getEmail() : null)
                // Sĩ số (computed)
                .siSoToiDa(toiDa)
                .siSoThucTe(thucTe)
                .siSoConLai(conLai)
                .phanTramDay(Math.round(phanTram * 10.0) / 10.0) // Làm tròn 1 chữ số thập phân
                .build();
    }

    /**
     * Build Sort an toàn với whitelist field name (chống SQL Injection qua sortBy).
     * Chỉ cho phép sort theo các field đã biết trước.
     */
    private Sort buildSort(String sortBy, String sortDir) {
        // Whitelist các field hợp lệ để sắp xếp (tránh inject field lạ)
        String safeField = switch (sortBy != null ? sortBy : "") {
            case "soTinChi"   -> "hocPhan.soTinChi";
            case "hocPhi"     -> "hocPhi";
            case "siSoConLai" -> "siSoToiDa"; // Proxy: sort theo toiDa vì conLai là computed
            case "maLopHp"    -> "maLopHp";
            default           -> "hocPhan.tenHocPhan"; // Default: tên môn A→Z
        };

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, safeField);
    }
}
