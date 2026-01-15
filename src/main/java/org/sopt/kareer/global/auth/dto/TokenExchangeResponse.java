package org.sopt.kareer.global.auth.dto;

public record TokenExchangeResponse(
        String accessToken,
        boolean onboardingRequired
) {
}
