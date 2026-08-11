package org.sopt.kareer.domain.roadmap.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.roadmap.facade.RoadmapGenerateFacade;
import org.sopt.kareer.domain.roadmap.service.RoadmapGenerationSseService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapControllerSseTest {

    @Mock
    private RoadmapGenerateFacade roadmapGenerateFacade;

    @Mock
    private RoadmapGenerationSseService roadmapGenerationSseService;

    @Mock
    private HttpServletResponse response;

    @DisplayName("로드맵 생성 SSE 연결을 열고 프록시 버퍼링 방지 헤더를 설정한다")
    @Test
    void generateRoadmapStream_returnsEmitterAndSetsHeaders() {
        RoadmapController controller =
                new RoadmapController(roadmapGenerateFacade, roadmapGenerationSseService);
        SseEmitter expected = new SseEmitter();
        when(roadmapGenerationSseService.generate(1L)).thenReturn(expected);

        SseEmitter actual = controller.generateRoadmapStream(1L, response);

        assertThat(actual).isSameAs(expected);
        verify(response).setHeader("Cache-Control", "no-cache");
        verify(response).setHeader("X-Accel-Buffering", "no");
    }
}
