package org.sopt.kareer.domain.member.dto.response;

import java.time.LocalDate;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;

public record MemberInfoResponse(
        Long memberId,
        String name,
        String profileImageUrl,
        LocalDate birthDate,
        Country country,
        String primaryMajor,
        String secondaryMajor,
        String targetJob,
        LocalDate graduationDate,
        LocalDate expectedGraduationDate,
        LanguageLevel languageLevel,
        String degree,
        String targetJobSkill
) {
    public static MemberInfoResponse fromEntity(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                member.getProfileImageUrl(),
                member.getBirthDate(),
                member.getCountry(),
                member.getPrimaryMajor(),
                member.getSecondaryMajor(),
                member.getTargetJob(),
                member.getGraduationDate(),
                member.getExpectedGraduationDate(),
                member.getLanguageLevel(),
                member.getDegree().getDescription(),
                member.getTargetJobSkill()
        );
    }
}
