package org.sopt.kareer.domain.member.service.dto.response;

import lombok.Builder;

@Builder
public record MemberCompletion(
        boolean onboardingRequired,

        boolean agreeTerm
) {
    public static MemberCompletion of(boolean onboardingRequired, boolean agreeTerm){
        return MemberCompletion.builder()
                .onboardingRequired(onboardingRequired)
                .agreeTerm(agreeTerm)
                .build();
    }
}
