package org.sopt.kareer.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.fixture.MemberOnboardRequestFixture;
import org.sopt.kareer.support.ControllerTestSupport;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerV2Test extends ControllerTestSupport {

    private UsernamePasswordAuthenticationToken authenticatedMember() {
        return new UsernamePasswordAuthenticationToken(1L, null, List.of());
    }

    @DisplayName("회원 온보딩 V2 정보를 저장한다.")
    @Test
    void onboardMember() throws Exception {
        willDoNothing().given(memberOnboardingFacade).onboard(any(), any());

        mockMvc.perform(post("/api/v2/members/onboard")
                        .with(authentication(authenticatedMember()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MemberOnboardRequestFixture.create())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 온보딩이 완료되었습니다."));
    }
}
