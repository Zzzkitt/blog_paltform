package com.blog.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;

    private String username;
    private String role;

    public JwtResponse(String token, Long expiresIn, String username, String role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
    }
}
