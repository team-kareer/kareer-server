package org.sopt.kareer.global.auth.dto;

public record LoginCodePayload(
        Long memberId,
        boolean onboardingRequired
) {
}
