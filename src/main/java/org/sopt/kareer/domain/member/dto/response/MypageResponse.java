package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;

import java.time.LocalDate;

@Builder
public record MypageResponse(

        @Schema(description = "이름", example = "홍승원")
        String name,

        @Schema(description = "프로필 사진 url")
        String profileImageUrl,

        @Schema(description = "목표 직무", example = "Developer")
        String targetJob,

        @Schema(description = "생년월일")
        LocalDate birthDate,

        @Schema(description = "국가")
        String country,

        @Schema(description = "학위")
        String degree,

        @Schema(description = "대학")
        String university,

        @Schema(description = "전공", example = "Computer Science")
        String primaryMajor,

        @Schema(description = "부전공", example = "Statistic")
        String secondaryMajor,

        @Schema(description = "비자 유형", example = "D-2")
        String visaType,

        @Schema(description = "비자 만료일")
        LocalDate visaExpiredAt,

        @Schema(description = "언어 수준")
        LanguageLevel languageLevel,

        @Schema(description = "영어 실력")
        String englishLevel

) {
    public static MypageResponse from(Member member, MemberVisa memberVisa) {
        return MypageResponse.builder()
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .targetJob(member.getTargetJob())
                .birthDate(member.getBirthDate())
                .country(member.getCountry().getCountryName())
                .degree(member.getDegree().getDescription())
                .university(member.getUniversity())
                .primaryMajor(member.getPrimaryMajor())
                .secondaryMajor(member.getSecondaryMajor())
                .visaType(memberVisa.getVisaType().getDescription())
                .visaExpiredAt(memberVisa.getVisaExpiredAt())
                .languageLevel(member.getLanguageLevel())
                .englishLevel(member.getEnglishLevel().getDescription())
                .build();
    }
}
