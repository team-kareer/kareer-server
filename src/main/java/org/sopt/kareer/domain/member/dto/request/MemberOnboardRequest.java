package org.sopt.kareer.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.VisaType;

public record MemberOnboardRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @NotNull(message = "생년월일은 필수 입력값입니다.")
        LocalDate birthDate,
        @NotNull(message = "국가는 필수 입력값입니다.")
        Country country,
        @NotNull(message = "언어 능력은 필수 입력값입니다.")
        LanguageLevel languageLevel,
        @NotNull(message = "학위는 필수 입력값입니다.")
        Degree degree,
        @NotNull(message = "비자 유형은 필수 입력값입니다.")
        VisaType visaType,
        @Schema(description = "예상 졸업일, D2 비자인 경우만", type = "string", format = "date", example = "2025-08-31")
        LocalDate expectedGraduationDate,
        @NotNull(message = "졸업일은 필수 입력값입니다.")
        LocalDate visaStartDate,
        @NotNull(message = "비자 만료일은 필수 입력값입니다.")
        LocalDate visaExpiredAt,
        @Schema(description = "비자 점수, D10 비자인 경우만", example = "50")
        Integer visaPoint,
        @NotBlank(message = "제1전공은 필수 입력값입니다.")
        String primaryMajor,
        String secondaryMajor,
        @NotBlank(message = "희망 직무는 필수 입력값입니다.")
        String targetJob,
        String targetJobSkill,
        @NotBlank(message = "개인 배경은 필수 입력값입니다.")
        @Size(max = 1000, message = "개인 배경은 최대 1000자까지 입력할 수 있습니다.")
        String personalBackground
) {

    @AssertTrue(message = "visaPoint는 D10 비자인 경우에만 입력할 수 있습니다.")
    @Schema(hidden = true)
    public boolean isVisaPointValid() {
        if (visaType == null) {
            return visaPoint == null;
        }
        if (visaType == VisaType.D10) {
            return visaPoint != null;
        }
        return visaPoint == null;
    }
}
