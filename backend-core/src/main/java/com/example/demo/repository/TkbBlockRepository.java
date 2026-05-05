package com.example.demo.repository;

import com.example.demo.domain.entity.TkbBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TkbBlockRepository extends JpaRepository<TkbBlock, Long> {

    List<TkbBlock> findByHocKy_IdHocKy(Long idHocKy);

    boolean existsByHocKy_IdHocKyAndMaBlock(Long hocKyId, String maBlock);
}
