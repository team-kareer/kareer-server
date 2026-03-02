package org.sopt.kareer.global.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
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
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.auth.dto.LoginCodePayload;
import org.sopt.kareer.global.auth.dto.request.TokenExchangeRequest;
import org.sopt.kareer.global.auth.dto.response.TokenExchangeResponse;
import org.sopt.kareer.global.auth.dto.response.TokenResponse;
import org.sopt.kareer.global.auth.util.RefreshTokenCookieManager;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.dto.JwtTokenDTO;
import org.sopt.kareer.global.jwt.dto.TokenType;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private MemberService memberService;

    @Mock
    private LoginCodeService loginCodeService;

    @Mock
    private RefreshTokenCookieManager refreshTokenCookieManager;

    @InjectMocks
    private AuthService authService;

    @DisplayName("Refresh Token 쿠키로 재발급하면 Access Token이 반환된다.")
    @Test
    void reissueWithValidRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshToken = "refresh-token";
        Long memberId = 1L;
        Member member = MemberFixture.getMember("provider-refresh");
        JwtTokenDTO jwtToken = new JwtTokenDTO("new-access", "new-refresh");

        given(refreshTokenCookieManager.read(request)).willReturn(Optional.of(refreshToken));
        given(jwtTokenUtil.extractMemberId(refreshToken, TokenType.REFRESH)).willReturn(memberId);
        given(memberService.getById(memberId)).willReturn(member);
        given(jwtTokenProvider.generate(member)).willReturn(jwtToken);

        TokenResponse responseDto = authService.reissue(request, response);

        assertThat(responseDto.accessToken()).isEqualTo(jwtToken.accessToken());
        verify(refreshTokenCookieManager).write(response, jwtToken.refreshToken());
    }

    @DisplayName("Refresh Token 쿠키가 없으면 재발급 시 UNAUTHORIZED를 던진다.")
    @Test
    void reissueWithoutRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        given(refreshTokenCookieManager.read(request)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("회원 조회에 실패하면 재발급 시 UNAUTHORIZED를 던진다.")
    @Test
    void reissueMemberNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshToken = "refresh-token";
        Long memberId = 10L;

        given(refreshTokenCookieManager.read(request)).willReturn(Optional.of(refreshToken));
        given(jwtTokenUtil.extractMemberId(refreshToken, TokenType.REFRESH)).willReturn(memberId);
        given(memberService.getById(memberId)).willThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        assertThatThrownBy(() -> authService.reissue(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("로그인 코드를 교환하면 Access Token과 온보딩 여부가 반환된다.")
    @Test
    void exchangeLoginCode() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        LoginCodePayload payload = new LoginCodePayload(2L, true);
        Member member = MemberFixture.getMember("provider-exchange");
        JwtTokenDTO jwtToken = new JwtTokenDTO("access-token", "refresh-token");

        given(loginCodeService.consume(request.code())).willReturn(payload);
        given(memberService.getById(payload.memberId())).willReturn(member);
        given(jwtTokenProvider.generate(member)).willReturn(jwtToken);

        TokenExchangeResponse responseDto = authService.exchange(request, response);

        assertThat(responseDto.accessToken()).isEqualTo(jwtToken.accessToken());
        assertThat(responseDto.onboardingRequired()).isTrue();
        verify(refreshTokenCookieManager).write(response, jwtToken.refreshToken());
    }

    @DisplayName("로그인 코드에 해당하는 회원이 없으면 교환 시 UNAUTHORIZED를 던진다.")
    @Test
    void exchangeLoginCodeMemberNotFound() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        TokenExchangeRequest request = new TokenExchangeRequest("login-code");
        LoginCodePayload payload = new LoginCodePayload(3L, false);

        given(loginCodeService.consume(request.code())).willReturn(payload);
        given(memberService.getById(payload.memberId())).willThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        assertThatThrownBy(() -> authService.exchange(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }
}
