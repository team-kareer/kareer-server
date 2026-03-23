package org.sopt.kareer.domain.roadmap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.support.ControllerTestSupport;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PhaseActionControllerTest extends ControllerTestSupport {

    @Nested
    @DisplayName("Ai 가이드를 조회한다.")
    class GetAiGuide {

        @Test
        @DisplayName("AI 가이드가 정상적으로 조회된다.")
        void getAiGuide_success() throws Exception {
            // given
            Long phaseActionId = 1L;

            AiGuideResponse response = new AiGuideResponse(
                    "test-importance",
                    List.of("test-mistake-1", "test-mistake-2"),
                    List.of("test-guideline-1", "test-guideline-2")
            );

            given(phaseActionService.getAiGuide(any(),eq(phaseActionId)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/phase-actions/{phaseActionId}/guide", phaseActionId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("AI 가이드가 조회되었습니다."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.mistakes").isArray())
                    .andExpect(jsonPath("$.data.mistakes.length()").value(2))
                    .andExpect(jsonPath("$.data.guidelines").isArray())
                    .andExpect(jsonPath("$.data.guidelines.length()").value(2));
            ;
        }

        @Test
        @DisplayName("존재하지 않는 PhaseAction에 대한 AI 가이드 조회 시 예외가 발생한다.")
        void getAiGuide_notFoundPhaseAction() throws Exception {
            // given
            Long phaseActionId = 0L;

            given(phaseActionService.getAiGuide(any(),eq(phaseActionId)))
                    .willThrow(new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/phase-actions/{phaseActionId}/guide", phaseActionId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND.getMessage()));
        }
    }
}
