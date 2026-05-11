package com.example.demo.domain.enums;

/**
 * Vòng đời công bố của lớp học phần (Sprint 3 — class opening lifecycle).
 *
 * <p>Khác với {@code LopHocPhan.trangThai} (vận hành: DANG_MO/HET_CHO/KHOA/DA_HUY),
 * {@code statusPublish} đánh dấu giai đoạn công bố:
 * <ol>
 *   <li>{@link #SHELL}: vừa sinh từ {@code spawn-shell}, chưa xếp lịch, chưa gán giảng viên.</li>
 *   <li>{@link #SCHEDULED}: đã có {@code thoiKhoaBieuJson} + {@code giangVien}, qua conflict-check.</li>
 *   <li>{@link #PUBLISHED}: được công bố cho pha B; sinh viên mới thấy và đăng ký được.</li>
 * </ol>
 *
 * <p>Trên các record dữ liệu cũ (trước Sprint 3), giá trị mặc định = {@link #PUBLISHED}
 * để giữ tương thích (back-compat).
 */
public enum LopHocPhanPublishStatus {
    SHELL,
    SCHEDULED,
    PUBLISHED
}
