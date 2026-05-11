package com.example.demo.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO chứa các tiêu chí lọc môn học của Sinh Viên.
 * Builder Pattern: Client ghép các filter tùy ý, không cần điền đủ mọi field.
 *
 * Luồng: Client → CourseSearchRequest → CourseSearchSpecification → JPQL/Criteria → DB
 *
 * SRP: Chỉ là container dữ liệu input, không chứa logic.
 * OCP: Thêm filter mới chỉ cần thêm field + xử lý trong Specification, không sửa Controller.
 */
@Getter
@Setter
@Builder
public class CourseSearchRequest {

    /** Từ khóa tự do: tìm theo tên môn hoặc mã môn (LIKE %keyword%) */
    private String keyword;

    /** Lọc theo ID học kỳ đang mở (bắt buộc có để tránh load toàn bộ lịch sử) */
    private Long idHocKy;

    /** Lọc theo học phần (lớp mở của đúng môn) — dùng khi ghép PRE intent → đăng ký chính thức. */
    private Long idHocPhan;

    /**
     * Khi không null: chỉ trả các lớp thuộc một trong các học phần này (server gán cho ROLE_STUDENT).
     * Rỗng → không có môn nào khớp (truy vấn trả về không dòng).
     */
    private List<Long> allowedHocPhanIds;

    /** Lọc theo ID Khoa (Khoa Công nghệ TT, Khoa Kinh tế...) */
    private Long idKhoa;

    /** Lọc theo số tín chỉ (1, 2, 3, 4...) */
    private Integer soTinChi;

    /** Lọc theo loại môn: BAT_BUOC, TU_CHON, DAI_CUONG, CHUYEN_NGANH */
    private String loaiMon;

    /** Lọc theo ID giảng viên */
    private Long idGiangVien;

    /**
     * Nếu true: Chỉ lấy lớp còn chỗ (siSoThucTe < siSoToiDa).
     * Default: false (lấy cả lớp hết chỗ để SV xem thông tin).
     */
    @Builder.Default
    private boolean chiConCho = false;

    /** Lọc theo trạng thái lớp: DANG_MO, HET_CHO, KHOA (mặc định chỉ DANG_MO) */
    @Builder.Default
    private String trangThai = "DANG_MO";

    // ── Phân trang ──────────────────────────────────────────────
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    /** Trường sắp xếp: tenHocPhan, soTinChi, siSoConLai, hocPhi */
    @Builder.Default
    private String sortBy = "tenHocPhan";

    /** ASC hoặc DESC */
    @Builder.Default
    private String sortDir = "ASC";
}
