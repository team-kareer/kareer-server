package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardUniversitiesResponse(
        List<LocalizedItemResponse> universities
) {
    public static OnboardUniversitiesResponse of(List<LocalizedItemResponse> universities) {
        return new OnboardUniversitiesResponse(universities);
    }
}
