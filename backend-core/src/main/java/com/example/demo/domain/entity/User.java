package com.example.demo.domain.entity;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tai_khoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private Status status;

    /** Email nhận mã OTP khi bật MFA (Task 22). */
    @Column(name = "email_otp", length = 255)
    private String email;

    @Column(name = "mfa_bat", nullable = false)
    @Builder.Default
    private boolean mfaEnabled = false;

    // Callbacks to ensure default values align with DB constraints (Liskov concept prep if subclassed)
    @PrePersist
    public void prePersist() {
        if (role == null) role = Role.STUDENT;
        if (status == null) status = Status.ACTIVE;
    }
}
