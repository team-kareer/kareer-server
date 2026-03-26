package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardMajorsResponse(
        List<LocalizedItemResponse> majors
) {
    public static OnboardMajorsResponse of(List<LocalizedItemResponse> majorList) {
        return new OnboardMajorsResponse(majorList);
    }
}
