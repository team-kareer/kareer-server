package org.sopt.kareer.domain.member.util;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.global.exception.errorcode.MemberErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberContextBuilder {
    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;

    public MemberAndContext load(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<MemberVisa> visas = memberVisaRepository.findAllByMemberIdAndVisaStatus(memberId, VisaStatus.ACTIVE);

        StringBuilder sb = new StringBuilder();
        sb.append("User Profile\n");
        sb.append("- name: ").append(nullToEmpty(member.getName())).append("\n");
        sb.append("- country: ").append(member.getCountry() != null ? member.getCountry().name() : "").append("\n");
        sb.append("- primaryMajor: ").append(nullToEmpty(member.getPrimaryMajor())).append("\n");
        sb.append("- secondaryMajor: ").append(nullToEmpty(member.getSecondaryMajor())).append("\n");
        sb.append("- targetJob: ").append(nullToEmpty(member.getTargetJob())).append("\n");
        sb.append("- languageLevel: ").append(member.getLanguageLevel() != null ? member.getLanguageLevel().name() : "").append("\n");
        sb.append("- degree: ").append(member.getDegree() != null ? member.getDegree().name() : "").append("\n");
        sb.append("- graduationDate: ").append(member.getGraduationDate() != null ? member.getGraduationDate() : "").append("\n");
        sb.append("- expectedGraduationDate: ").append(member.getExpectedGraduationDate() != null ? member.getExpectedGraduationDate() : "").append("\n");
        sb.append("- targetJobSkill: ").append(nullToEmpty(member.getTargetJobSkill())).append("\n");
        sb.append("- personalBackground: ").append(nullToEmpty(member.getPersonalBackground())).append("\n");

        sb.append("Visa Info\n");
        for (MemberVisa v : visas) {
            sb.append("- visaType: ").append(v.getVisaType().name())
                    .append(", visaStatus: ").append(v.getVisaStatus().name())
                    .append(", visaStartDate: ").append(v.getVisaStartDate())
                    .append(", visaExpiredAt: ").append(v.getVisaExpiredAt())
                    .append(", visaPoint: ").append(v.getVisaPoint() != null ? v.getVisaPoint() : "")
                    .append("\n");
        }

        return new MemberAndContext(member, sb.toString());
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public record MemberAndContext(Member member, String contextText) {}
}
