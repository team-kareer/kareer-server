package org.sopt.kareer.global.jwt.dto;

public record JwtToken(
        String accessToken, String refreshToken
) {
}
