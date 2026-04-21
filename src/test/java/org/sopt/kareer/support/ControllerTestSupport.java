package org.sopt.kareer.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sopt.kareer.domain.jobposting.controller.JobPostingController;
import org.sopt.kareer.domain.jobposting.service.JobPostingCrawler;
import org.sopt.kareer.domain.jobposting.service.JobPostingService;
import org.sopt.kareer.domain.member.controller.MemberController;
import org.sopt.kareer.domain.member.controller.MemberControllerV2;
import org.sopt.kareer.domain.member.facade.MemberFacade;
import org.sopt.kareer.domain.member.facade.MemberOcrFacade;
import org.sopt.kareer.domain.member.facade.MemberOnboardingFacade;
import org.sopt.kareer.domain.member.service.LocalizedOnboardQueryService;
import org.sopt.kareer.domain.roadmap.controller.PhaseActionController;
import org.sopt.kareer.domain.roadmap.controller.PhaseController;
import org.sopt.kareer.domain.roadmap.facade.PhaseActionFacade;
import org.sopt.kareer.domain.roadmap.facade.PhaseFacade;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.external.discord.client.DiscordClient;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@WebMvcTest(controllers = {JobPostingController.class, MemberController.class, MemberControllerV2.class, PhaseController.class, PhaseActionController.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)
        })
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(ControllerTestSupport.TestSecurityWebMvcConfig.class)
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JobPostingService jobPostingService;

    @MockBean
    protected JobPostingCrawler jobPostingCrawler;

    @MockBean
    protected JwtTokenUtil jwtTokenUtil;

    @MockBean
    protected MemberFacade memberFacade;

    @MockBean
    protected MemberOcrFacade memberOcrFacade;

    @MockBean
    protected MemberOnboardingFacade memberOnboardingFacade;

    @MockBean
    protected LocalizedOnboardQueryService localizedOnboardQueryService;

    @MockBean
    protected JpaMetamodelMappingContext mappingContext;

    @MockBean
    protected DiscordClient discordClient;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected PhaseFacade phaseFacade;

    @MockBean
    protected PhaseActionFacade phaseActionFacade;

    @TestConfiguration
    static class TestSecurityWebMvcConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }
}
