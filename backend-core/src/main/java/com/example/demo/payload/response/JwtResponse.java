package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private List<String> roles;
    
    // Custom return type header
    private final String type = "Bearer";
}
