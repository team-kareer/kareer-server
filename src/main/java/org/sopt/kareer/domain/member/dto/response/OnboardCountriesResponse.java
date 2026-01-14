package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardCountriesResponse(
        List<String> countries
) {
    public static OnboardCountriesResponse from(List<String> countries) {
        return new OnboardCountriesResponse(countries);
    }
}
