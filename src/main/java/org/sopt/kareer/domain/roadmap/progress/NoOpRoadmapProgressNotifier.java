package org.sopt.kareer.domain.roadmap.progress;

public enum NoOpRoadmapProgressNotifier implements RoadmapProgressNotifier {

    INSTANCE;

    @Override
    public void started(RoadmapGenerationStep step) {
    }

    @Override
    public void completed(RoadmapGenerationStep step) {
    }

    @Override
    public void failed(RoadmapGenerationStep step) {
    }
}
