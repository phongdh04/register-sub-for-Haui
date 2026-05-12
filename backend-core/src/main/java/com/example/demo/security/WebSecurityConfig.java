package com.example.demo.security;

import com.example.demo.security.jwt.AuthTokenFilter;
import com.example.demo.security.jwt.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Facade & Proxy Pattern configuration mapping.
 * Hides complex security initialization logic.
 */
@Configuration
@EnableMethodSecurity // Enables @PreAuthorize proxy checks
public class WebSecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      // Pass UserDetailsService to constructor or properly initialize it
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
      return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Spring Boot 3.4+ Syntax avoids deprecated .and()
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // Data Master: GET public; POST/PUT/DELETE controlled by @PreAuthorize
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/khoa/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/nganh-dao-tao/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/hoc-phan/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/hoc-ky/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/giang-vien/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/lop-hoc-phan/**").permitAll()
                .anyRequest().authenticated()
        );

    http.authenticationProvider(authenticationProvider());
    
    // Chain of Responsibility: Insert our JWT filter before the standard UsernamePassword filter
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174"));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
      configuration.setAllowedHeaders(Arrays.asList(
              "Authorization", "Content-Type", "Idempotency-Key", "x-requested-with", "Cache-Control"));
      configuration.setAllowCredentials(true);
      
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
}
