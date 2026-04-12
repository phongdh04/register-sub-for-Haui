package com.example.demo.repository;

import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// SRP: Repository only handles database read/writes for User/Tai_Khoan
// DIP: Spring proxies this interface during runtime via @Autowired dependency injection.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Optimized Query Method generated tightly by Spring Data JPA
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
