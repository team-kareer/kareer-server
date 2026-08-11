package org.sopt.kareer.domain.roadmap.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;

import static org.assertj.core.api.Assertions.assertThat;

class RoadmapProgressEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자_분석_시작_이벤트를_직렬화한다() throws Exception {
        RoadmapProgressEvent event = RoadmapProgressEvent.started(
                RoadmapGenerationStep.USER_ANALYSIS
        );

        String json = objectMapper.writeValueAsString(event);

        assertThat(json)
                .contains("\"sequence\":1")
                .contains("\"step\":\"USER_ANALYSIS\"")
                .contains("\"status\":\"STARTED\"");
    }

    @Test
    void 정책_검색_완료_이벤트를_생성한다() {
        RoadmapProgressEvent event = RoadmapProgressEvent.completed(
                RoadmapGenerationStep.POLICY_SEARCH
        );

        assertThat(event.sequence()).isEqualTo(2);
        assertThat(event.step()).isEqualTo(RoadmapGenerationStep.POLICY_SEARCH);
        assertThat(event.status().name()).isEqualTo("COMPLETED");
    }
}
