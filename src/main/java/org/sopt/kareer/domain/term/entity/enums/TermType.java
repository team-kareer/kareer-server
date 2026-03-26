package org.sopt.kareer.domain.term.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermType {

    SERVICE(1),
    PERSONAL(2),
    IDENTIFICATION(3),
    SENSITIVE(4),
    ENTRUSTMENT(5),
    MARKETING(6);

    private final int order;
}
