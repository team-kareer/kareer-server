package org.sopt.kareer.global.external.ai.builder.context;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static org.sopt.kareer.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class MemberContextBuilder {
    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;

    public MemberAndContext load(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        List<MemberVisa> visas = memberVisaRepository.findAllByMemberIdAndVisaStatus(memberId, VisaStatus.ACTIVE);

        StringBuilder sb = new StringBuilder();
        sb.append("User Profile\n");
        appendLine(sb, "name", member.getName());
        appendLine(sb, "email", member.getEmail());
        appendLine(sb, "birthDate", member.getBirthDate());
        appendLine(sb, "country", member.getCountry() != null ? member.getCountry().name() : "");
        appendLine(sb, "university", member.getUniversity());
        appendLine(sb, "primaryMajor", member.getPrimaryMajor());
        appendLine(sb, "secondaryMajor", member.getSecondaryMajor());
        appendLine(sb, "targetJob", member.getTargetJob());
        appendLine(sb, "targetJobSkill", member.getTargetJobSkill());
        appendLine(sb, "fieldsOfInterest", member.getFieldsOfInterest());
        appendLine(sb, "preparationStatus", member.getPreparationStatus());
        appendLine(sb, "personalBackground", member.getPersonalBackground());
        appendLine(sb, "languageLevel", member.getLanguageLevel() != null ? member.getLanguageLevel().name() : "");
        appendLine(sb, "englishLevel", member.getEnglishLevel() != null ? member.getEnglishLevel().name() : "");
        appendLine(sb, "degree", member.getDegree() != null ? member.getDegree().name() : "");
        appendLine(sb, "graduationDate", member.getGraduationDate());
        appendLine(sb, "expectedGraduationDate", member.getExpectedGraduationDate());
        appendLine(sb, "preparationStatus",  member.getPreparationStatus());

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

    private void appendLine(StringBuilder sb, String key, String value) {
        sb.append("- ").append(key).append(": ").append(nullToEmpty(value)).append("\n");
    }

    private void appendLine(StringBuilder sb, String key, LocalDate value) {
        sb.append("- ").append(key).append(": ").append(toText(value)).append("\n");
    }

    private String toText(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    public record MemberAndContext(Member member, String contextText) {}
}
