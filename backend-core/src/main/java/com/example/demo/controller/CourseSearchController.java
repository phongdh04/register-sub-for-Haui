package com.example.demo.controller;

import com.example.demo.payload.request.CourseSearchRequest;
import com.example.demo.payload.response.CourseSearchResponse;
import com.example.demo.service.ICourseSearchService;
import com.example.demo.service.StudentCurriculumCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * REST Controller – Tìm kiếm Lớp Học Phần (Task 6).
 *
 * Base path: /api/v1/courses
 * Quyền: STUDENT (xem lớp để đăng ký) + ADMIN (quản lý danh mục) + LECTURER (tra cứu lớp phụ trách).
 *
 * SRP: Chỉ nhận request HTTP, gọi service, trả response. Không chứa logic nghiệp vụ.
 * DIP: Phụ thuộc ICourseSearchService (interface), không phụ thuộc impl cụ thể.
 *
 * API Design:
 *   GET /api/v1/courses           → Search + Filter + Phân trang (query params)
 *   GET /api/v1/courses/{id}      → Chi tiết một lớp học phần
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseSearchController {

    private final ICourseSearchService courseSearchService;
    private final StudentCurriculumCourseService studentCurriculumCourseService;

    /**
     * API tìm kiếm lớp học phần với nhiều filter.
     *
     * Ví dụ request:
     *   GET /api/v1/courses?keyword=Lập+trình&idHocKy=1&soTinChi=3&chiConCho=true&page=0&size=20
     *
     * @param keyword      Từ khóa (tên môn, mã môn, mã lớp)
     * @param idHocKy      ID học kỳ đang xem
     * @param idHocPhan    Lọc các lớp thuộc đúng học phần (intent PRE → đăng ký chính thức)
     * @param idKhoa       ID khoa (lọc theo phòng ban)
     * @param soTinChi     Số tín chỉ (1-5)
     * @param loaiMon      BAT_BUOC | TU_CHON | DAI_CUONG | CHUYEN_NGANH
     * @param idGiangVien  ID giảng viên phụ trách
     * @param chiConCho    true: chỉ lấy lớp còn chỗ
     * @param trangThai    DANG_MO | HET_CHO | KHOA (default: DANG_MO)
     * @param page         Trang (0-indexed)
     * @param size         Số item/trang (max 100)
     * @param sortBy       Field sắp xếp: tenHocPhan | soTinChi | hocPhi | maLopHp
     * @param sortDir      ASC | DESC
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'LECTURER')")
    public ResponseEntity<Page<CourseSearchResponse>> searchCourses(
            Authentication authentication,
            @RequestParam(required = false)                 String  keyword,
            @RequestParam(required = false)                 Long    idHocKy,
            @RequestParam(required = false)                 Long    idHocPhan,
            @RequestParam(required = false)                 Long    idKhoa,
            @RequestParam(required = false)                 Integer soTinChi,
            @RequestParam(required = false)                 String  loaiMon,
            @RequestParam(required = false)                 Long    idGiangVien,
            @RequestParam(defaultValue = "false")           boolean chiConCho,
            @RequestParam(defaultValue = "DANG_MO")         String  trangThai,
            @RequestParam(defaultValue = "0")               int     page,
            @RequestParam(defaultValue = "20")              int     size,
            @RequestParam(defaultValue = "tenHocPhan")      String  sortBy,
            @RequestParam(defaultValue = "ASC")             String  sortDir) {

        CourseSearchRequest.CourseSearchRequestBuilder rb = CourseSearchRequest.builder()
                .keyword(keyword)
                .idHocKy(idHocKy)
                .idHocPhan(idHocPhan)
                .idKhoa(idKhoa)
                .soTinChi(soTinChi)
                .loaiMon(loaiMon)
                .idGiangVien(idGiangVien)
                .chiConCho(chiConCho)
                .trangThai(trangThai)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir);

        if (authentication != null && restrictsToStudentProgram(authentication)) {
            rb.allowedHocPhanIds(studentCurriculumCourseService.listAllowedHocPhanIdsForSearch(
                    authentication.getName(), idHocKy));
        }

        CourseSearchRequest request = rb.build();

        Page<CourseSearchResponse> result = courseSearchService.searchCourses(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Sinh viên thuần (không ADMIN/LECTURER) chỉ xem các lớp thuộc học phần trong CTĐT ngành.
     */
    private static boolean restrictsToStudentProgram(Authentication authentication) {
        boolean student = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a != null ? a.toUpperCase(Locale.ROOT) : "")
                .anyMatch("ROLE_STUDENT"::equals);
        boolean elevation = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a != null ? a.toUpperCase(Locale.ROOT) : "")
                .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ROLE_LECTURER".equals(a));
        return student && !elevation;
    }

    /**
     * API lấy chi tiết một lớp học phần theo ID.
     * SV dùng để xem TKB đầy đủ + điều kiện học trước khi bấm "Đăng ký".
     *
     * Ví dụ: GET /api/v1/courses/42
     */
    @GetMapping("/{idLopHp}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'LECTURER')")
    public ResponseEntity<CourseSearchResponse> getCourseDetail(
            @PathVariable Long idLopHp) {
        CourseSearchResponse detail = courseSearchService.getCourseDetail(idLopHp);
        return ResponseEntity.ok(detail);
    }
}
