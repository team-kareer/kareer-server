package org.sopt.kareer.global.oauth.response;

import org.sopt.kareer.global.auth.dto.TokenResponse;

public record OAuth2LoginResponse(
        Long memberId,
        boolean onboardingRequired,
        TokenResponse tokenResponse
) {
}
