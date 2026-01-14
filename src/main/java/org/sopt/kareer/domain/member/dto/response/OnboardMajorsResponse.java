package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardMajorsResponse(
        List<String> majors
) {
    public static OnboardMajorsResponse from(List<String> majorList) {
        return new OnboardMajorsResponse(majorList);
    }
}
