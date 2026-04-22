package com.example.demo.repository;

import com.example.demo.domain.entity.Lop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopRepository extends JpaRepository<Lop, Long> {
    boolean existsByMaLop(String maLop);
}
