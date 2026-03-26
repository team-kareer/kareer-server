package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record OnboardFieldsResponse(
        List<LocalizedItemResponse> fields
) {
    public static OnboardFieldsResponse of(List<LocalizedItemResponse> fields) {
        return new OnboardFieldsResponse(fields);
    }
}
