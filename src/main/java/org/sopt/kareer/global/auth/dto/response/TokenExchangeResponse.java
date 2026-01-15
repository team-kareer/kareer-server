package org.sopt.kareer.global.auth.dto.response;

public record TokenExchangeResponse(
        String accessToken,
        boolean onboardingRequired
) {
}
