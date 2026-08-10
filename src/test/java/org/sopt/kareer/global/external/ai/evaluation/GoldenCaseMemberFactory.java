package org.sopt.kareer.global.external.ai.evaluation;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;

import java.time.LocalDate;

public class GoldenCaseMemberFactory {

    private GoldenCaseMemberFactory() {
    }

    public static Member toMember(GoldenCase goldenCase) {
        return toMember(goldenCase, goldenCase.caseId());
    }

    public static Member toMember(GoldenCase goldenCase, String uniqueSuffix) {
        return Member.builder()
                .name("ragas-eval-" + uniqueSuffix)
                .email("ragas-eval-" + uniqueSuffix + "@example.com")
                .provider(OAuthProvider.GOOGLE)
                .providerId("ragas-eval-" + uniqueSuffix)
                .status(MemberStatus.ACTIVE)
                .targetJob(goldenCase.targetJob())
                .degreeCode(goldenCase.degreeCode())
                .expectedGraduationDate(goldenCase.expectedGraduationDate())
                .build();
    }

    public static MemberVisa toVisa(Member member, GoldenCase goldenCase) {
        return MemberVisa.builder()
                .member(member)
                .visaType(goldenCase.visaType())
                .visaStatus(VisaStatus.ACTIVE)
                .visaStartDate(LocalDate.now().minusMonths(3))
                .visaExpiredAt(LocalDate.now().plusYears(1))
                .build();
    }
}
