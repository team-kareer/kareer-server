package org.sopt.kareer.domain.roadmap.progress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoadmapGenerationStep {

    USER_ANALYSIS(1),
    POLICY_SEARCH(2),
    ROADMAP_WRITING(3);

    private final int sequence;
}
