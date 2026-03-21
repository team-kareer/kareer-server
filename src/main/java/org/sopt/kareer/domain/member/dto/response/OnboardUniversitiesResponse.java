package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardUniversitiesResponse(
        List<String> universities
) {
    public static OnboardUniversitiesResponse from(List<String> universities) {
        return new OnboardUniversitiesResponse(universities);
    }
}
