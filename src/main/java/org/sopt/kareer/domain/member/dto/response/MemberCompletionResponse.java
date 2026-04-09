package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberCompletionResponse(
        @Schema(description = "온보딩 여부")
        boolean onboardingRequired,

        @Schema(description = "약관 동의 여부")
        boolean agreeTerm
) {
    public static MemberCompletionResponse of(boolean onboardingRequired, boolean agreeTerm){
        return MemberCompletionResponse.builder()
                .onboardingRequired(onboardingRequired)
                .agreeTerm(agreeTerm)
                .build();
    }
}
