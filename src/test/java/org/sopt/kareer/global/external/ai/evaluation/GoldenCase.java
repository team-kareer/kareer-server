package org.sopt.kareer.global.external.ai.evaluation;

import org.sopt.kareer.domain.member.entity.enums.VisaType;

import java.time.LocalDate;

public record GoldenCase(
        String caseId,
        String description,
        String targetJob,
        String degreeCode,
        LocalDate expectedGraduationDate,
        VisaType visaType,
        String referenceAnswer
) {
}
