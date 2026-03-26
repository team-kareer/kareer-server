package org.sopt.kareer.domain.member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VisaType {
    D2("D-2"),
    D10("D-10"),
    E7("E-7")
    ;

    private final String description;

    public static VisaType from(String originalText) {
        if (originalText == null || originalText.isBlank()) {
            return null;
        }

        String normalized = normalize(originalText);

        for (VisaType visaType : values()) {
            if (normalize(visaType.name()).equals(normalized)
                || normalize(visaType.description).equals(normalized)) {
                return visaType;
            }
        }

        return null;
    }

    private static String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }
}
