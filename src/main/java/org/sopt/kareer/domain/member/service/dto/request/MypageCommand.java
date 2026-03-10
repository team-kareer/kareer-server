package org.sopt.kareer.domain.member.service.dto.request;

import org.sopt.kareer.domain.member.entity.enums.*;

import java.time.LocalDate;

public record MypageCommand(
        String targetJob,

        LocalDate birthDate,

        Country country,

        Degree degree,

        String university,

        String primaryMajor,

        String secondaryMajor,

        VisaType visaType,

        LocalDate visaExpiredAt,

        LanguageLevel languageLevel,

        EnglishLevel englishLevel
) {

}
