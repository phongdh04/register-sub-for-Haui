package com.example.demo.repository;

import com.example.demo.domain.entity.NganhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NganhDaoTaoRepository extends JpaRepository<NganhDaoTao, Long> {
    Optional<NganhDaoTao> findByMaNganh(String maNganh);
    List<NganhDaoTao> findByKhoa_IdKhoa(Long idKhoa);
    boolean existsByMaNganh(String maNganh);
}
