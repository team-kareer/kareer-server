package org.sopt.kareer.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.member.entity.enums.*;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;

import java.time.LocalDate;

public record MypageRequest(
        @Schema(description = "목표 직무", example = "Developer")
        String targetJob,

        @Schema(description = "생년월일")
        LocalDate birthDate,

        @Schema(description = "국가", example = "Afghanistan")
        Country country,

        @Schema(description = "학위", example = "DOMESTIC_ASSOCIATE")
        Degree degree,

        @Schema(description = "대학", example = "Konkuk University")
        String university,

        @Schema(description = "전공", example = "Computer Science")
        String primaryMajor,

        @Schema(description = "부전공", example = "Statistic")
        String secondaryMajor,

        @Schema(description = "비자 유형", example = "D2")
        VisaType visaType,

        @Schema(description = "비자 만료일", example = "2027-01-01")
        LocalDate visaExpiredAt,

        @Schema(description = "언어 수준", example = "LEVEL_3")
        LanguageLevel languageLevel,

        @Schema(description = "영어 실력", example = "BEGINNER")
        EnglishLevel englishLevel
) {
        public MypageCommand toCommand(){
                return new MypageCommand(this.targetJob, this.birthDate, this.country,
                        this.degree, this.university, this.primaryMajor, this.secondaryMajor, this.visaType,
                        this.visaExpiredAt, this.languageLevel, this.englishLevel);
        }
}
