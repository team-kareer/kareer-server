package org.sopt.kareer.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import org.sopt.kareer.domain.member.entity.enums.*;

public record MemberOnboardV2Request(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @NotNull(message = "생년월일은 필수 입력값입니다.")
        LocalDate birthDate,

        @NotNull(message = "대학교 코드는 필수 입력값입니다.")
        String universityCode,

        @NotNull(message = "국가 코드는 필수 입력값입니다.")
        String countryCode,

        @NotNull(message = "언어 능력은 필수 입력값입니다.")
        LanguageLevel languageLevel,

        @NotNull(message = "영어 능력은 필수 입력값입니다.")
        EnglishLevel englishLevel,

        @NotNull(message = "학위는 필수 입력값입니다.")
        Degree degree,

        @NotNull(message = "비자 유형은 필수 입력값입니다.")
        VisaType visaType,

        @Schema(description = "예상 졸업일, D2 비자인 경우만", type = "string", format = "date", example = "2025-08-31")
        LocalDate expectedGraduationDate,

        @NotNull(message = "비자 시작일은 필수 입력값입니다.")
        LocalDate visaStartDate,

        @NotNull(message = "비자 만료일은 필수 입력값입니다.")
        LocalDate visaExpiredAt,

        @NotBlank(message = "제1전공 코드는 필수 입력값입니다.")
        String primaryMajorCode,

        String secondaryMajor,

        @NotNull(message = "관심 분야는 필수 입력값입니다.")
        @Size(min = 1, max = 5, message = "관심 분야는 최소 1개, 최대 5개까지 선택할 수 있습니다.")
        List<String> fieldsOfInterests,

        List<String> preparationStatuses,

        @NotBlank(message = "희망 직무는 필수 입력값입니다.")
        String targetJob,

        String targetJobSkill,

        @NotBlank(message = "개인 배경은 필수 입력값입니다.")
        @Size(max = 1000, message = "개인 배경은 최대 1000자까지 입력할 수 있습니다.")
        String personalBackground
) {
}
