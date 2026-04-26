package org.sopt.kareer.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.MemberStatusResponse;
import org.sopt.kareer.domain.member.dto.response.MypageResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.sopt.kareer.domain.member.facade.MemberFacade;
import org.sopt.kareer.domain.member.facade.MemberOcrFacade;
import org.sopt.kareer.domain.member.facade.MemberOnboardingFacade;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;
import org.sopt.kareer.domain.member.service.LocalizedOnboardQueryService;
import org.sopt.kareer.global.auth.facade.AuthFacade;
import org.sopt.kareer.support.ControllerTestSupport;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MemberControllerTest extends ControllerTestSupport {

    @MockBean
    private MemberFacade memberFacade;

    @MockBean
    private MemberOcrFacade memberOcrFacade;

    @MockBean
    private MemberOnboardingFacade memberOnboardingFacade;

    @MockBean
    private LocalizedOnboardQueryService localizedOnboardQueryService;

    @MockBean
    private AuthFacade authFacade;

    private UsernamePasswordAuthenticationToken authenticatedMember() {
        return new UsernamePasswordAuthenticationToken(1L, null, List.of());
    }

    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMemberInfo() throws Exception {
        MemberInfoResponse response = new MemberInfoResponse(
                1L,
                "test-user",
                "test-user@example.com",
                "profile-image-url",
                LocalDate.of(2000, 4, 1),
                "United States",
                "Computer Science",
                "Math",
                "Backend",
                "Seoul National University",
                LocalDate.of(2023, 2, 1),
                LocalDate.of(2024, 2, 1),
                LanguageLevel.LEVEL_4,
                "outside-korea-bachelor",
                "advanced",
                "Java",
                VisaType.D2
        );
        given(memberFacade.getMemberInfo(any())).willReturn(response);

        mockMvc.perform(get("/api/v1/members/me")
                        .with(authentication(authenticatedMember())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data.memberId").value(response.memberId()));
    }

    @DisplayName("온보딩 국가 목록을 조회한다.")
    @Test
    void getOnboardCountries() throws Exception {
        mockMvc.perform(get("/api/v1/members/onboard/countries"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("온보딩 전공 목록을 조회한다.")
    @Test
    void getOnboardMajors() throws Exception {
        mockMvc.perform(get("/api/v1/members/onboard/majors"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원 상태 정보를 조회한다.")
    @Test
    void getMemberStatus() throws Exception {
        MemberStatusResponse response = MemberStatusResponse.builder()
                .visaType(VisaType.D2)
                .visaExpiredAt(LocalDate.now().plusMonths(6))
                .expectedGraduationDate(LocalDate.of(2024, 2, 1))
                .graduationDate(LocalDate.of(2023, 2, 1))
                .build();
        given(memberFacade.getMemberStatus(any())).willReturn(response);

        mockMvc.perform(get("/api/v1/members/me/status")
                        .with(authentication(authenticatedMember())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.visaType").value("D2"));
    }

    @DisplayName("마이페이지를 조회한다.")
    @Test
    void getMyPage() throws Exception {
        //given
        Member member = MemberFixture.getMember();
        MemberVisa memberVisa = MemberVisaFixture.activeD2(member);

        MypageResponse response = MypageResponse.of(
                member, memberVisa,
                member.getCountryCode(),
                member.getPrimaryMajorCode(),
                member.getUniversityCode(),
                member.getDegreeCode(),
                member.getEnglishLevelCode()
        );
        given(memberFacade.getMypage(any())).willReturn(response);

        //when && then
        mockMvc.perform(get("/api/v1/members/mypage")
                        .with(authentication(authenticatedMember())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(member.getName()));
    }
}
