package com.tradesphere.ai.identity.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String message;
    private String userId;
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}

