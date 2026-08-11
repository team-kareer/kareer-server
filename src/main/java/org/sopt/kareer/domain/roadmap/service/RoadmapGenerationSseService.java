package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapGenerationCompletedEvent;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapGenerationFailedEvent;
import org.sopt.kareer.domain.roadmap.facade.RoadmapGenerateFacade;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.SseRoadmapProgressNotifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapGenerationSseService {

    private static final long SSE_TIMEOUT_MILLIS = 5 * 60 * 1000L;
    private static final String COMPLETED_EVENT_NAME = "roadmap-completed";
    private static final String FAILED_EVENT_NAME = "roadmap-failed";

    private final RoadmapGenerateFacade roadmapGenerateFacade;
    private final ExecutorService executorService;

    public SseEmitter generate(Long memberId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        SseRoadmapProgressNotifier notifier = new SseRoadmapProgressNotifier(emitter);

        emitter.onCompletion(notifier::disconnect);
        emitter.onTimeout(() -> {
            notifier.disconnect();
            emitter.complete();
        });
        emitter.onError(exception -> notifier.disconnect());

        executorService.submit(() -> generateRoadmap(memberId, emitter, notifier));
        return emitter;
    }

    private void generateRoadmap(
            Long memberId,
            SseEmitter emitter,
            SseRoadmapProgressNotifier notifier
    ) {
        try {
            roadmapGenerateFacade.generateRoadmap(memberId, notifier);
            send(emitter, notifier, COMPLETED_EVENT_NAME, RoadmapGenerationCompletedEvent.completed());
        } catch (RuntimeException exception) {
            RoadmapGenerationStep failedStep = notifier.currentStep() == null
                    ? RoadmapGenerationStep.USER_ANALYSIS
                    : notifier.currentStep();
            send(emitter, notifier, FAILED_EVENT_NAME, RoadmapGenerationFailedEvent.of(failedStep));
            log.error("[ROADMAP_SSE] roadmap generation failed: memberId={}, step={}",
                    memberId, failedStep, exception);
        } finally {
            emitter.complete();
        }
    }

    private void send(
            SseEmitter emitter,
            SseRoadmapProgressNotifier notifier,
            String eventName,
            Object data
    ) {
        if (!notifier.isConnected()) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException | IllegalStateException exception) {
            notifier.disconnect();
        }
    }
}
