package org.sopt.kareer.global.auth.facade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.global.auth.dto.LoginCodePayload;
import org.sopt.kareer.global.auth.dto.request.TokenExchangeRequest;
import org.sopt.kareer.global.auth.dto.response.TokenExchangeResponse;
import org.sopt.kareer.global.auth.dto.response.TokenResponse;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.auth.service.LoginCodeService;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.dto.JwtTokenDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private AuthService authService;

    @Mock
    private MemberQueryService memberQueryService;

    @Mock
    private LoginCodeService loginCodeService;

    @InjectMocks
    private AuthFacade authFacade;

    @DisplayName("Refresh Token 쿠키로 재발급하면 Access Token이 반환된다.")
    @Test
    void reissue_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Long memberId = 1L;
        Member member = MemberFixture.getMember("provider-refresh");
        JwtTokenDTO jwtToken = new JwtTokenDTO("new-access", "new-refresh");

        given(authService.extractMemberIdFromRefreshCookie(request)).willReturn(memberId);
        given(memberQueryService.getMemberById(memberId)).willReturn(member);
        given(authService.generateToken(member, response)).willReturn(jwtToken);

        TokenResponse result = authFacade.reissue(request, response);

        assertThat(result.accessToken()).isEqualTo(jwtToken.accessToken());
    }

    @DisplayName("Refresh Token 쿠키가 없으면 재발급 시 UNAUTHORIZED를 던진다.")
    @Test
    void reissue_noRefreshToken_throws() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(authService.extractMemberIdFromRefreshCookie(request))
                .willThrow(new GlobalException(GlobalErrorCode.UNAUTHORIZED));

        assertThatThrownBy(() -> authFacade.reissue(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("회원 조회에 실패하면 재발급 시 UNAUTHORIZED를 던진다.")
    @Test
    void reissue_memberNotFound_throws() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Long memberId = 10L;

        given(authService.extractMemberIdFromRefreshCookie(request)).willReturn(memberId);
        given(memberQueryService.getMemberById(memberId))
                .willThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        assertThatThrownBy(() -> authFacade.reissue(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("로그인 코드를 교환하면 Access Token과 온보딩 여부가 반환된다.")
    @Test
    void exchange_success() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        LoginCodePayload payload = new LoginCodePayload(2L, true);
        Member member = MemberFixture.getMember("provider-exchange");
        JwtTokenDTO jwtToken = new JwtTokenDTO("access-token", "refresh-token");

        given(loginCodeService.consume(request.code())).willReturn(payload);
        given(memberQueryService.getMemberById(payload.memberId())).willReturn(member);
        given(authService.generateToken(member, response)).willReturn(jwtToken);

        TokenExchangeResponse result = authFacade.exchange(request, response);

        assertThat(result.accessToken()).isEqualTo(jwtToken.accessToken());
        assertThat(result.onboardingRequired()).isTrue();
    }

    @DisplayName("로그인 코드에 해당하는 회원이 없으면 교환 시 UNAUTHORIZED를 던진다.")
    @Test
    void exchange_memberNotFound_throws() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        LoginCodePayload payload = new LoginCodePayload(3L, false);

        given(loginCodeService.consume(request.code())).willReturn(payload);
        given(memberQueryService.getMemberById(payload.memberId()))
                .willThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        assertThatThrownBy(() -> authFacade.exchange(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("로그아웃 요청을 AuthService에 위임한다.")
    @Test
    void signOut_delegates_to_authService() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        authFacade.signOut(request, response);

        then(authService).should().signOut(request, response);
    }
}
