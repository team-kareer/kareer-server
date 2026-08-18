package org.sopt.kareer.domain.roadmap.progress;

public interface RoadmapProgressNotifier {

    void started(RoadmapGenerationStep step);

    void completed(RoadmapGenerationStep step);

    void failed(RoadmapGenerationStep step);
}
