package com.example.demo.repository.specification;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.CourseSearchRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification - Builder Pattern cho Dynamic Query tìm kiếm môn học.
 *
 * Đây là trái tim của Task 6. Thay vì viết hàng chục method query cứng (findByX, findByXAndY...),
 * dùng Specification để ghép Predicate động: chỉ những filter nào có giá trị mới được thêm vào WHERE.
 *
 * Ưu điểm kỹ thuật (thích hợp để trình bày với Hội đồng):
 * - OCP: Thêm filter mới → thêm predicate, không sửa query cũ.
 * - SRP: Chỉ xây dựng điều kiện WHERE, không xử lý pagination hay sort.
 * - Tận dụng INDEX trên idx_lhp_hoc_ky, idx_lhp_hoc_phan (đã định nghĩa trong entity).
 *
 * Kỹ thuật JOIN sử dụng:
 * - LopHocPhan JOIN HocPhan (lấy tenHocPhan, maHocPhan, soTinChi, loaiMon)
 * - LopHocPhan JOIN HocKy   (lấy tenHocKy, filter theo idHocKy)
 * - LopHocPhan JOIN GiangVien (lấy tenGiangVien)
 * - HocPhan JOIN NganhDaoTao → Khoa (filter theo idKhoa - nested join)
 */
public class CourseSearchSpecification {

    private CourseSearchSpecification() {
        // Utility class - không khởi tạo
    }

    /**
     * Build Specification từ CourseSearchRequest.
     * Mỗi filter chỉ được thêm vào khi có giá trị (null-safe).
     *
     * @param request DTO chứa các tiêu chí lọc từ Client.
     * @return Specification<LopHocPhan> sẵn sàng truyền vào repository.findAll(spec, pageable).
     */
    public static Specification<LopHocPhan> buildFrom(CourseSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── JOIN sẵn để tránh N+1 query ────────────────────────
            Join<Object, Object> hocPhanJoin   = root.join("hocPhan",   JoinType.INNER);
            Join<Object, Object> hocKyJoin     = root.join("hocKy",     JoinType.INNER);
            Join<Object, Object> giangVienJoin = root.join("giangVien", JoinType.LEFT);

            // ── Distinct để tránh duplicate khi JOIN nhiều bảng ────
            if (query != null) {
                query.distinct(true);
            }

            // ── FILTER 1: Keyword (tìm theo tên môn HOẶC mã môn) ──
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String pattern = "%" + request.getKeyword().trim().toLowerCase() + "%";
                Predicate byTenMon = cb.like(cb.lower(hocPhanJoin.get("tenHocPhan")), pattern);
                Predicate byMaMon  = cb.like(cb.lower(hocPhanJoin.get("maHocPhan")),  pattern);
                Predicate byMaLop  = cb.like(cb.lower(root.get("maLopHp")),           pattern);
                predicates.add(cb.or(byTenMon, byMaMon, byMaLop)); // OR của 3 điều kiện
            }

            // ── FILTER 2: Học Kỳ (quan trọng nhất, có INDEX) ──────
            if (request.getIdHocKy() != null) {
                predicates.add(cb.equal(hocKyJoin.get("idHocKy"), request.getIdHocKy()));
            }

            // ── FILTER 3: Số tín chỉ ──────────────────────────────
            if (request.getSoTinChi() != null) {
                predicates.add(cb.equal(hocPhanJoin.get("soTinChi"), request.getSoTinChi()));
            }

            // ── FILTER 4: Loại môn ────────────────────────────────
            if (request.getLoaiMon() != null && !request.getLoaiMon().isBlank()) {
                predicates.add(cb.equal(hocPhanJoin.get("loaiMon"), request.getLoaiMon()));
            }

            // ── FILTER 5: Giảng viên ──────────────────────────────
            if (request.getIdGiangVien() != null) {
                predicates.add(cb.equal(giangVienJoin.get("idGiangVien"), request.getIdGiangVien()));
            }

            // ── FILTER 6: Trạng thái lớp ─────────────────────────
            if (request.getTrangThai() != null && !request.getTrangThai().isBlank()) {
                predicates.add(cb.equal(root.get("trangThai"), request.getTrangThai()));
            }

            // ── FILTER 7: Chỉ lấy lớp còn chỗ ───────────────────
            if (request.isChiConCho()) {
                // siSoThucTe < siSoToiDa
                predicates.add(cb.lessThan(root.get("siSoThucTe"), root.get("siSoToiDa")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
