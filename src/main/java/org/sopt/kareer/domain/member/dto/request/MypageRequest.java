package org.sopt.kareer.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.kareer.domain.member.entity.enums.*;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;

import java.time.LocalDate;

public record MypageRequest(

        @Schema(description = "목표 직무", example = "Developer")
        @NotBlank(message = "targetJob은 필수 입력값입니다.")
        String targetJob,

        @Schema(description = "생년월일")
        @NotNull(message = "생년월일은 필수 입력값입니다.")
        LocalDate birthDate,

        @Schema(description = "국가", example = "Afghanistan")
        @NotNull(message = "국가는 필수 입력값입니다.")
        Country country,

        @Schema(description = "학위", example = "DOMESTIC_ASSOCIATE")
        @NotNull(message = "학위는 필수 입력값입니다.")
        Degree degree,

        @Schema(description = "대학", example = "Konkuk University")
        @NotBlank(message = "대학은 필수 입력값입니다.")
        String university,

        @Schema(description = "전공", example = "Computer Science")
        @NotBlank(message = "전공은 필수 입력값입니다.")
        String primaryMajor,

        @Schema(description = "부전공", example = "Statistic")
        @NotBlank(message = "부전공은 필수 입력값입니다.")
        String secondaryMajor,

        @Schema(description = "비자 유형", example = "D2")
        @NotNull(message = "비자 유형은 필수 입력값입니다.")
        VisaType visaType,

        @Schema(description = "비자 만료일", example = "2027-01-01")
        @NotNull(message = "비자 만료일은 필수 입력값입니다.")
        LocalDate visaExpiredAt,

        @Schema(description = "언어 수준", example = "LEVEL_3")
        @NotNull(message = "언어 수준은 필수 입력값입니다.")
        LanguageLevel languageLevel,

        @Schema(description = "영어 실력", example = "BEGINNER")
        @NotNull(message = "영어 실력은 필수 입력값입니다.")
        EnglishLevel englishLevel
) {
        public MypageCommand toCommand(){
                return new MypageCommand(this.targetJob, this.birthDate, this.country,
                        this.degree, this.university, this.primaryMajor, this.secondaryMajor, this.visaType,
                        this.visaExpiredAt, this.languageLevel, this.englishLevel);
        }
}
