package org.sopt.kareer.domain.roadmap.progress;

import org.sopt.kareer.domain.roadmap.dto.response.RoadmapProgressEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SseRoadmapProgressNotifier implements RoadmapProgressNotifier {

    private static final String EVENT_NAME = "roadmap-progress";

    private final SseEmitter emitter;
    private final AtomicBoolean connected = new AtomicBoolean(true);
    private final AtomicReference<RoadmapGenerationStep> currentStep = new AtomicReference<>();

    public SseRoadmapProgressNotifier(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void started(RoadmapGenerationStep step) {
        currentStep.set(step);
        send(RoadmapProgressEvent.started(step));
    }

    @Override
    public void completed(RoadmapGenerationStep step) {
        send(RoadmapProgressEvent.completed(step));
    }

    @Override
    public void failed(RoadmapGenerationStep step) {
        currentStep.set(step);
        send(RoadmapProgressEvent.failed(step));
    }

    public RoadmapGenerationStep currentStep() {
        return currentStep.get();
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void disconnect() {
        connected.set(false);
    }

    private void send(RoadmapProgressEvent event) {
        if (!connected.get()) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(EVENT_NAME)
                    .data(event));
        } catch (IOException | IllegalStateException e) {
            disconnect();
        }
    }
}
