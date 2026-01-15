package org.sopt.kareer.global.auth.dto;

import org.sopt.kareer.global.jwt.dto.JwtToken;

public record TokenResponse(
        String accessToken
) {
    public static TokenResponse from(String accessToken) {
        return new TokenResponse(accessToken);
    }

    public static TokenResponse of(JwtToken jwtToken) {
        return from(jwtToken.accessToken());
    }
}
