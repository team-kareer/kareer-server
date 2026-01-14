package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;

@Schema(description = "회원 정보 응답")
public record MemberInfoResponse(
        @Schema(description = "회원 id", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile.png")
        String profileImageUrl,

        @Schema(description = "생년월일", example = "2000-05-02")
        LocalDate birthDate,

        @Schema(description = "거주 국가")
        Country country,

        @Schema(description = "주 전공", example = "Computer Science")
        String primaryMajor,

        @Schema(description = "부 전공", example = "Statistics")
        String secondaryMajor,

        @Schema(description = "타겟 직무", example = "Backend Engineer")
        String targetJob,

        @Schema(description = "졸업일", example = "2026-02-11")
        LocalDate graduationDate,

        @Schema(description = "예상 졸업일", example = "2026-02-11")
        LocalDate expectedGraduationDate,

        @Schema(description = "언어 수준")
        LanguageLevel languageLevel,

        @Schema(description = "학위", example = "Bachelor")
        String degree,

        @Schema(description = "타겟 직무 스킬", example = "Java, Spring")
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
