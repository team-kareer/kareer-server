package org.sopt.kareer.domain.roadmap.progress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SseRoadmapProgressNotifierTest {

    private SseEmitter emitter;
    private SseRoadmapProgressNotifier notifier;

    @BeforeEach
    void setUp() {
        emitter = mock(SseEmitter.class);
        notifier = new SseRoadmapProgressNotifier(emitter);
    }

    @Test
    void 단계_시작_이벤트를_SSE로_전송한다() throws Exception {
        notifier.started(RoadmapGenerationStep.USER_ANALYSIS);

        verify(emitter, times(1))
                .send(any(SseEmitter.SseEventBuilder.class));
        assertThat(notifier.currentStep())
                .isEqualTo(RoadmapGenerationStep.USER_ANALYSIS);
    }

    @Test
    void 연결이_종료되면_이벤트를_더_보내지_않는다() throws Exception {
        notifier.disconnect();

        notifier.completed(RoadmapGenerationStep.USER_ANALYSIS);

        verify(emitter, times(0))
                .send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void SSE_전송_실패는_비즈니스_예외로_전파하지_않는다() throws Exception {
        doThrow(new IOException("disconnected"))
                .when(emitter)
                .send(any(SseEmitter.SseEventBuilder.class));

        assertThatCode(() -> notifier.started(
                RoadmapGenerationStep.USER_ANALYSIS
        )).doesNotThrowAnyException();

        assertThat(notifier.isConnected()).isFalse();
    }
}
