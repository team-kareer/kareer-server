package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.member.service.dto.response.MemberCompletion;

@Builder
public record MemberCompletionResponse(
        @Schema(description = "온보딩 여부")
        boolean onboardingRequired,

        @Schema(description = "약관 동의 여부")
        boolean agreeTerm
) {

    public static MemberCompletionResponse toResponse(MemberCompletion memberCompletion){
        return MemberCompletionResponse.builder()
                .onboardingRequired(memberCompletion.onboardingRequired())
                .agreeTerm(memberCompletion.agreeTerm())
                .build();
    }
}
