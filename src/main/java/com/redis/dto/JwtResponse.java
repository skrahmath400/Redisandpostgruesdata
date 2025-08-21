package com.redis.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private final String accessToken;
    private final long expiresAtEpochSeconds;
    private final String tokenType;
    
}
