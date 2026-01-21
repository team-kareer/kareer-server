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
}
