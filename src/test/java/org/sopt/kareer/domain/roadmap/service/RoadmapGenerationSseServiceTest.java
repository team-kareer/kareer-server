package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.roadmap.facade.RoadmapGenerateFacade;
import org.sopt.kareer.domain.roadmap.progress.SseRoadmapProgressNotifier;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoadmapGenerationSseServiceTest {

    @Mock
    private RoadmapGenerateFacade roadmapGenerateFacade;

    @Mock
    private ExecutorService executorService;

    @DisplayName("SSE 연결을 반환하고 로드맵 생성을 별도 스레드에서 시작한다")
    @Test
    void generate_startsAsyncRoadmapGeneration() {
        RoadmapGenerationSseService service =
                new RoadmapGenerationSseService(roadmapGenerateFacade, executorService);

        SseEmitter emitter = service.generate(1L);

        assertThat(emitter).isNotNull();

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorService).submit(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(roadmapGenerateFacade)
                .generateRoadmap(eq(1L), any(SseRoadmapProgressNotifier.class));
    }
}
