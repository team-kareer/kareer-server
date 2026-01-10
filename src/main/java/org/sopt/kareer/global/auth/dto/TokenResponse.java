package org.sopt.kareer.global.auth.dto;

import org.sopt.kareer.global.jwt.dto.JwtToken;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenResponse of(JwtToken jwtToken) {
        return new TokenResponse(jwtToken.accessToken(), jwtToken.refreshToken());
    }
}
