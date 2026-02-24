package org.sopt.kareer.global.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.global.auth.dto.request.TokenExchangeRequest;
import org.sopt.kareer.global.auth.dto.response.TokenExchangeResponse;
import org.sopt.kareer.global.auth.dto.response.TokenResponse;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.sopt.kareer.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @DisplayName("로그인 코드를 Access Token으로 교환한다.")
    @Test
    void exchangeLoginCode() throws Exception {
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        TokenExchangeResponse response = new TokenExchangeResponse("access-token", true);
        given(authService.exchange(any(TokenExchangeRequest.class), any(HttpServletResponse.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/auth/code/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 코드가 교환되었습니다."))
                .andExpect(jsonPath("$.data.accessToken").value(response.accessToken()))
                .andExpect(jsonPath("$.data.onboardingRequired").value(response.onboardingRequired()));

        verify(authService).exchange(any(TokenExchangeRequest.class), any(HttpServletResponse.class));
    }

    @DisplayName("로그인 코드 요청 값이 비어있으면 400을 반환한다.")
    @Test
    void exchangeLoginCodeValidation() throws Exception {
        mockMvc.perform(post("/api/v1/auth/code/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @DisplayName("로그인 코드 교환 중 인증 오류가 발생하면 401을 반환한다.")
    @Test
    void exchangeLoginCodeUnauthorized() throws Exception {
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        given(authService.exchange(any(TokenExchangeRequest.class), any(HttpServletResponse.class)))
                .willThrow(new GlobalException(GlobalErrorCode.UNAUTHORIZED));

        mockMvc.perform(post("/api/v1/auth/code/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.UNAUTHORIZED.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.UNAUTHORIZED.getMessage()));

        verify(authService).exchange(any(TokenExchangeRequest.class), any(HttpServletResponse.class));
    }

    @DisplayName("Refresh Token 쿠키를 이용해 Access Token을 재발급한다.")
    @Test
    void reissueToken() throws Exception {
        TokenResponse response = TokenResponse.from("new-access-token");
        given(authService.reissue(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/auth/reissue"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("토큰이 재발급되었습니다."))
                .andExpect(jsonPath("$.data.accessToken").value(response.accessToken()));

        verify(authService).reissue(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
