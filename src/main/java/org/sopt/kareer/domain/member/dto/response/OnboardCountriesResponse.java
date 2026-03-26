package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardCountriesResponse(
        List<LocalizedItemResponse> countries
) {
    public static OnboardCountriesResponse of(List<LocalizedItemResponse> countries) {
        return new OnboardCountriesResponse(countries);
    }
}
