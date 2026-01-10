package org.sopt.kareer.global.oauth.response;

public record OAuth2LoginResponse(
        Long memberId,
        boolean onboardingRequired,
        String accessToken,
        String refreshToken
) {
}
