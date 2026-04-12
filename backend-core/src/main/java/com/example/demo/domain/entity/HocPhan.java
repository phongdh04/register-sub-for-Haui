package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

/**
 * Bảng Học Phần (Course/Subject).
 * OCP: Mở rộng thuộc tính môn học qua JSONB (thuoc_tinh_json, dieu_kien_rang_buoc_json)
 *      mà không cần ALTER TABLE hay sửa logic cũ.
 * Builder Pattern: Dùng Lombok Builder để ghép nối đối tượng phức tạp.
 */
@Entity
@Table(name = "Hoc_Phan", indexes = {
    @Index(name = "idx_hoc_phan_ma", columnList = "ma_hoc_phan")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoc_phan")
    private Long idHocPhan;

    @Column(name = "ma_hoc_phan", unique = true, nullable = false, length = 30)
    private String maHocPhan;

    @Column(name = "ten_hoc_phan", nullable = false, length = 300)
    private String tenHocPhan;

    @Column(name = "ma_in", length = 30) // Mã in trên Thời Khóa Biểu
    private String maIn;

    @Column(name = "so_tin_chi", nullable = false)
    private Integer soTinChi;

    @Column(name = "loai_mon", length = 50) // BAT_BUOC, TU_CHON, DAI_CUONG, CHUYEN_NGANH
    private String loaiMon;

    /**
     * JSONB: Chứa thuộc tính động (mo_ta, chuan_dau_ra, tai_lieu, bo_mon...).
     * Approach NoSQL trên Relational DB - Tránh JOIN sang bảng phụ.
     * OCP: Thêm field mới vào JSON mà không cần thay đổi schema.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "thuoc_tinh_json", columnDefinition = "jsonb")
    private Map<String, Object> thuocTinhJson;

    /**
     * JSONB: Điều kiện ràng buộc của môn học.
     * Format mẫu: {"tien_quyet": ["CS101"], "song_hanh": [], "thay_the": [], "tuong_duong": []}
     * Chain of Responsibility Pattern sẽ đọc JSON này để validate khi SV đăng ký.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dieu_kien_rang_buoc_json", columnDefinition = "jsonb")
    private Map<String, Object> dieuKienRangBuocJson;

    @OneToMany(mappedBy = "hocPhan", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans;
}
