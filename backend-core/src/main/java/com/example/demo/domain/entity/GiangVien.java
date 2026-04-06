package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Giảng Viên (Lecturer).
 * SRP: Chỉ chứa thông tin GV, không kiêm logic chấm điểm hay xếp lịch.
 * LSP: Tách riêng khỏi User để tránh GV kế thừa sai method của SV (đăng ký môn).
 */
@Entity
@Table(name = "Giang_Vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giang_vien")
    private Long idGiangVien;

    @Column(name = "ma_giang_vien", unique = true, nullable = false, length = 20)
    private String maGiangVien;

    @Column(name = "ten_giang_vien", nullable = false, length = 200)
    private String tenGiangVien;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "hoc_ham_hoc_vi", length = 100) // PGS.TS, TS, ThS...
    private String hocHamHocVi;

    // DIP: FK tới Khoa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khoa")
    private Khoa khoa;

    // Link tới tài khoản đăng nhập (nếu cần)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id")
    private User taiKhoan;

    @OneToMany(mappedBy = "giangVien", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans;
}
