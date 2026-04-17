package com.example.demo.controller;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.MfaVerifyRequest;
import com.example.demo.payload.response.JwtResponse;
import com.example.demo.payload.response.MfaLoginChallengeResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.jwt.UserDetailsImpl;
import com.example.demo.service.IMfaOtpService;
import com.example.demo.util.EmailMaskUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade Pattern: The AuthController hides the complexity of authentication manager,
 * JWT token generation, and repository interaction from the frontend.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IMfaOtpService mfaOtpService;

    @Autowired
    private UserDetailsService userDetailsService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalStateException("User missing sau khi xác thực"));

        boolean adminMfaActive = user.getRole() == Role.ADMIN
                && user.isMfaEnabled()
                && user.getEmail() != null
                && !user.getEmail().isBlank();

        if (adminMfaActive) {
            SecurityContextHolder.clearContext();
            String challengeId = mfaOtpService.createChallenge(user);
            return ResponseEntity.ok(new MfaLoginChallengeResponse(
                    true,
                    challengeId,
                    EmailMaskUtil.mask(user.getEmail()),
                    user.getUsername(),
                    "Mã đã gửi tới email đã cấu hình. Môi trường demo: xem log console backend nếu chưa có SMTP."));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    /** Task 22 – Hoàn tất đăng nhập sau khi nhập OTP email. */
    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        User user = mfaOtpService.validateAndConsume(request.getChallengeId(), request.getOtp());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);
        UserDetailsImpl ud = (UserDetailsImpl) userDetails;
        List<String> roles = ud.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, ud.getId(), ud.getUsername(), roles));
    }

    // Temporary POST /api/auth/seed (to create an initial admin account for testing)
    @PostMapping("/seed")
    public ResponseEntity<?> seedAdmin() {
        if (userRepository.existsByUsername("admin")) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = User.builder()
                .username("admin")
                .password(encoder.encode("123456"))
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .email("admin@eduport.demo")
                .mfaEnabled(false)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("Admin user seeded successfully!");
    }
}
