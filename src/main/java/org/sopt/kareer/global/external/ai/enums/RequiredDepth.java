package org.sopt.kareer.global.external.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequiredDepth {
    D1("Action Required", 1),
    D2_1("AI Guide & Risk", 2),
    D2_2("To-do List", 3);

    private final String label;
    private final int order;

}
