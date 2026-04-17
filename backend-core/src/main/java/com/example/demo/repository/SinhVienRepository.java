package com.example.demo.repository;

import com.example.demo.domain.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho bảng Sinh_Vien.
 * SRP: Chỉ truy xuất dữ liệu SinhVien, không xử lý logic nghiệp vụ.
 */
@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {

    Optional<SinhVien> findByMaSinhVien(String maSinhVien);
    Optional<SinhVien> findByTaiKhoan_Id(Long userId);

    @Query("""
            SELECT DISTINCT s FROM SinhVien s
            LEFT JOIN FETCH s.lop l
            LEFT JOIN FETCH l.nganhDaoTao n
            LEFT JOIN FETCH n.khoa k
            LEFT JOIN FETCH s.coVanHocTap cv
            LEFT JOIN FETCH s.hoSoSinhVien hsv
            WHERE s.taiKhoan.id = :userId
            """)
    Optional<SinhVien> findWithProfileByTaiKhoanId(@Param("userId") Long userId);

    boolean existsByMaSinhVien(String maSinhVien);

    @Query("""
            SELECT k.idKhoa, k.maKhoa, k.tenKhoa, COUNT(sv.idSinhVien)
            FROM SinhVien sv JOIN sv.lop l JOIN l.nganhDaoTao n JOIN n.khoa k
            GROUP BY k.idKhoa, k.maKhoa, k.tenKhoa
            ORDER BY COUNT(sv.idSinhVien) DESC
            """)
    List<Object[]> countStudentsByKhoa();
}
