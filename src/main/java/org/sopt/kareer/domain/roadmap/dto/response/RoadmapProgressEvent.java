package org.sopt.kareer.domain.roadmap.dto.response;

import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.RoadmapProgressStatus;

public record RoadmapProgressEvent(
        int sequence,
        RoadmapGenerationStep step,
        RoadmapProgressStatus status
) {

    public static RoadmapProgressEvent started(RoadmapGenerationStep step) {
        return new RoadmapProgressEvent(
                step.getSequence(),
                step,
                RoadmapProgressStatus.STARTED
        );
    }

    public static RoadmapProgressEvent completed(RoadmapGenerationStep step) {
        return new RoadmapProgressEvent(
                step.getSequence(),
                step,
                RoadmapProgressStatus.COMPLETED
        );
    }

    public static RoadmapProgressEvent failed(RoadmapGenerationStep step) {
        return new RoadmapProgressEvent(
                step.getSequence(),
                step,
                RoadmapProgressStatus.FAILED
        );
    }
}
