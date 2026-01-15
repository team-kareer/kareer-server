package org.sopt.kareer.global.auth.dto;

import org.sopt.kareer.global.jwt.dto.JwtTokenDTO;

public record TokenResponse(
        String accessToken
) {
    public static TokenResponse from(String accessToken) {
        return new TokenResponse(accessToken);
    }

    public static TokenResponse of(JwtTokenDTO jwtTokenDTO) {
        return from(jwtTokenDTO.accessToken());
    }
}
