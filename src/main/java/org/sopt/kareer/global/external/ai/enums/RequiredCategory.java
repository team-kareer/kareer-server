package org.sopt.kareer.global.external.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequiredCategory {
    VISA("VISA_PDF"),
    CAREER("CAREER_PDF")
    ;
    private final String description;
}
