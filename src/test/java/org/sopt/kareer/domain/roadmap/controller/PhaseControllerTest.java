package org.sopt.kareer.domain.roadmap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.fixture.PhaseResponseFixture;
import org.sopt.kareer.domain.roadmap.service.PhaseService;
import org.sopt.kareer.support.ControllerTestSupport;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class PhaseControllerTest extends ControllerTestSupport {

    @MockBean
    protected PhaseService phaseService;

    @DisplayName("Phase 리스트를 성공적으로 조회한다.")
    @Test
    void getPhaseList_success() throws Exception {
        // given
        PhaseResponse phase1 = PhaseResponseFixture.of();
        PhaseResponse phase2 = PhaseResponseFixture.of();
        PhaseResponse phase3 = PhaseResponseFixture.of();

        given(phaseService.getPhases(any()))
                .willReturn(new PhaseListResponse(List.of(phase1, phase2, phase3)));

        // when & then
        mockMvc.perform(get("/api/v1/phases"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Phase 리스트가 조회되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.phases").isArray())
                .andExpect(jsonPath("$.data.phases.length()").value(3))
                .andExpect(jsonPath("$.data.phases[0].startDate").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$.data.phases[0].endDate").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
        ;
    }

    @Nested
    @DisplayName("로드맵 Phase 상세정보를 조회한다.")
    class GetRoadmapPhaseDetail {

        @Test
        @DisplayName("로드맵 phase 상세정보가 정상적으로 조회된다.")
        void getRoadmapPhaseDetail_success() throws Exception {
            // given
            Long phaseId = 1L;

            // ActionResponse 리스트 생성
            List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse> actionsList = List.of(
                    new RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse(
                            1L,
                            "test-title",
                            "test-description",
                            LocalDate.of(2026, 3, 5),
                            true
                    )
            );

            // ActionGroupResponse 맵 생성
            Map<String, RoadmapPhaseDetailResponse.ActionGroupResponse> actionsMap = Map.of(
                    "Visa", new RoadmapPhaseDetailResponse.ActionGroupResponse(1L, actionsList)
            );

            // Response 생성
            RoadmapPhaseDetailResponse response = new RoadmapPhaseDetailResponse(1L, actionsMap);

            given(phaseService.getRoadmapPhaseDetail(any(), eq(phaseId)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/phases/{phaseId}/roadmap", phaseId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("로드맵 Phase 상세정보가 조회되었습니다."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.actions.Visa.items[0].deadline").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
            ;
        }

        @Test
        @DisplayName("존재하지 않는 로드맵 Phase 상세정보 조회 시 예외가 발생한다.")
        void getRoadmapPhaseDetail_notFondPhase() throws Exception {
            // given
            Long phaseId = 0L;

            given(phaseService.getRoadmapPhaseDetail(any(), eq(phaseId)))
                    .willThrow(new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/phases/{phaseId}/roadmap", phaseId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("존재하지 않는 Phase입니다."));
        }
    }
}
