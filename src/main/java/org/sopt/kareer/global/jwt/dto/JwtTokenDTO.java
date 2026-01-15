package org.sopt.kareer.global.jwt.dto;

public record JwtTokenDTO(
        String accessToken,
        String refreshToken
) {
}
