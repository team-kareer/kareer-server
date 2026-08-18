package org.sopt.kareer.domain.roadmap.dto.response;

import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;

public record RoadmapGenerationFailedEvent(
        RoadmapGenerationStep step,
        String status,
        String code,
        String message
) {

    public static RoadmapGenerationFailedEvent of(RoadmapGenerationStep step) {
        return new RoadmapGenerationFailedEvent(
                step,
                "FAILED",
                "ROADMAP_GENERATION_FAILED",
                "로드맵 생성에 실패했습니다."
        );
    }
}
