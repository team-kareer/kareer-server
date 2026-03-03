package org.sopt.kareer.domain.roadmap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.fixture.PhaseResponseFixture;
import org.sopt.kareer.domain.roadmap.service.PhaseService;
import org.sopt.kareer.support.ControllerTestSupport;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

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
                .andExpect(jsonPath("$.data.phases.length()").value(3));
                .andExpect(jsonPath("$.data.phases.length()").value(3))
                .andExpect(jsonPath("$.data.phases[0].startDate").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$.data.phases[0].endDate").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
        ;
    }

    }
}
