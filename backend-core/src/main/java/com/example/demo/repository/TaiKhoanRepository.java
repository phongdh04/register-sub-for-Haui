package com.example.demo.repository;

import com.example.demo.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByRole(com.example.demo.domain.enums.Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<User> searchByRole(@Param("role") com.example.demo.domain.enums.Role role,
                             @Param("q") String query,
                             Pageable pageable);
}
