package com.example.demo.service;

import com.example.demo.payload.request.CourseSearchRequest;
import com.example.demo.payload.response.CourseSearchResponse;
import org.springframework.data.domain.Page;

/**
 * Interface cho chức năng tìm kiếm môn học (Task 6).
 *
 * DIP: Controller phụ thuộc vào interface này, không phụ thuộc impl cụ thể.
 * ISP: Tách riêng khỏi ILopHocPhanService (CRUD admin), vì search có luồng data khác.
 */
public interface ICourseSearchService {

    /**
     * Tìm kiếm lớp học phần theo nhiều tiêu chí động.
     * Trả về Page để hỗ trợ phân trang (Frontend lazy-load).
     *
     * @param request DTO chứa từ khóa + bộ lọc + thông tin phân trang.
     * @return Page<CourseSearchResponse> chứa danh sách kết quả và metadata phân trang.
     */
    Page<CourseSearchResponse> searchCourses(CourseSearchRequest request);

    /**
     * Lấy chi tiết một lớp học phần theo ID.
     * Dùng khi SV click vào một lớp để xem lịch học chi tiết trước khi bấm Đăng ký.
     *
     * @param idLopHp ID lớp học phần cần xem.
     * @return CourseSearchResponse đầy đủ thông tin.
     */
    CourseSearchResponse getCourseDetail(Long idLopHp);
}
