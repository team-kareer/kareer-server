package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardFieldsResponse(
        List<String> fields
) {
    public static OnboardFieldsResponse from(List<String> fields) {
        return new OnboardFieldsResponse(fields);
    }
}
