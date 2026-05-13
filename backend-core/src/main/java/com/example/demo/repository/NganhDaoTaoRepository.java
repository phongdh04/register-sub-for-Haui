package com.example.demo.repository;

import com.example.demo.domain.entity.NganhDaoTao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NganhDaoTaoRepository extends JpaRepository<NganhDaoTao, Long> {
    Optional<NganhDaoTao> findByMaNganh(String maNganh);
    List<NganhDaoTao> findByKhoa_IdKhoa(Long idKhoa);
    Page<NganhDaoTao> findByKhoa_IdKhoa(Long idKhoa, Pageable pageable);
    boolean existsByMaNganh(String maNganh);
}
