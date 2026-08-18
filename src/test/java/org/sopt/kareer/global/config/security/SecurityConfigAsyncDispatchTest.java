package org.sopt.kareer.global.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.roadmap.controller.PhaseController;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.facade.PhaseFacade;
import org.sopt.kareer.global.external.discord.client.DiscordClient;
import org.sopt.kareer.global.jwt.filter.JwtAuthenticationFilter;
import org.sopt.kareer.global.jwt.filter.JwtExceptionFilter;
import org.sopt.kareer.global.jwt.handler.CustomAccessDeniedHandler;
import org.sopt.kareer.global.jwt.handler.CustomAuthenticationEntryPoint;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.sopt.kareer.global.oauth.handler.OAuth2AuthenticationFailureHandler;
import org.sopt.kareer.global.oauth.handler.OAuth2AuthenticationSuccessHandler;
import org.sopt.kareer.global.oauth.service.CustomOidcOAuth2UserService;
import org.sopt.kareer.global.security.filter.OnboardingRestrictionFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhaseController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({SecurityConfig.class, CorsConfig.class})
class SecurityConfigAsyncDispatchTest {

    @jakarta.annotation.Resource
    private MockMvc mockMvc;

    @MockBean
    private PhaseFacade phaseFacade;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private MemberQueryService memberQueryService;

    @MockBean
    private JpaMetamodelMappingContext mappingContext;

    @MockBean
    private DiscordClient discordClient;

    @MockBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockBean
    private CorsProperties corsProperties;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtExceptionFilter jwtExceptionFilter;

    @MockBean
    private OnboardingRestrictionFilter onboardingRestrictionFilter;

    @MockBean
    private CustomOidcOAuth2UserService customOidcOAuth2UserService;

    @MockBean
    private OAuth2AuthenticationSuccessHandler successHandler;

    @MockBean
    private OAuth2AuthenticationFailureHandler failureHandler;

    @BeforeEach
    void letSecurityFiltersContinue() throws Exception {
        doAnswer(invocation -> {
            invocation.<jakarta.servlet.http.HttpServletResponse>getArgument(1)
                    .setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(authenticationEntryPoint).commence(any(), any(), any());

        doAnswer(invocation -> {
            invocation.<jakarta.servlet.http.HttpServletResponse>getArgument(1)
                    .setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
            return null;
        }).when(accessDeniedHandler).handle(any(), any(), any());

        doAnswer(invocation -> {
            invocation.<jakarta.servlet.FilterChain>getArgument(2)
                    .doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            invocation.<jakarta.servlet.FilterChain>getArgument(2)
                    .doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtExceptionFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            invocation.<jakarta.servlet.FilterChain>getArgument(2)
                    .doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(onboardingRestrictionFilter).doFilter(any(), any(), any());
    }

    @DisplayName("인증이 끝난 SSE의 ASYNC 재디스패치는 다시 인증하지 않는다")
    @Test
    void asyncDispatch_isPermittedWithoutReauthentication() throws Exception {
        given(phaseFacade.getPhases(nullable(Long.class)))
                .willReturn(new PhaseListResponse(List.of()));

        mockMvc.perform(get("/api/v1/roadmap/phases")
                        .with(request -> {
                            request.setDispatcherType(jakarta.servlet.DispatcherType.ASYNC);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Phase 리스트가 조회되었습니다."));
    }

    @DisplayName("일반 요청은 인증 없이 접근할 수 없다")
    @Test
    void normalRequest_stillRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/roadmap/phases"))
                .andExpect(status().isUnauthorized());
    }
}
