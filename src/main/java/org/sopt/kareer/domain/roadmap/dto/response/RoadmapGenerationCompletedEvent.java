package org.sopt.kareer.domain.roadmap.dto.response;

public record RoadmapGenerationCompletedEvent(String status) {

    public static RoadmapGenerationCompletedEvent completed() {
        return new RoadmapGenerationCompletedEvent("COMPLETED");
    }
}
