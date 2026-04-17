package com.example.demo.repository;

import com.example.demo.domain.entity.LichThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichThiRepository extends JpaRepository<LichThi, Long> {

    Optional<LichThi> findByLopHocPhan_IdLopHp(Long idLopHp);

    List<LichThi> findByLopHocPhan_IdLopHpIn(Collection<Long> idLopHps);
}
