package org.sopt.kareer.global.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.global.auth.util.RefreshTokenCookieManager;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.dto.TokenType;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private RefreshTokenCookieManager refreshTokenCookieManager;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    @DisplayName("로그아웃 시 쿠키를 삭제하고 액세스 토큰을 블랙리스트에 등록한다.")
    @Test
    void signOut_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String accessToken = "access-token";
        long remainingSeconds = 300L;

        given(jwtTokenUtil.resolveToken(request)).willReturn(Optional.of(accessToken));
        given(jwtTokenUtil.extractRemainingValiditySeconds(accessToken, TokenType.ACCESS)).willReturn(remainingSeconds);

        authService.signOut(request, response);

        then(refreshTokenCookieManager).should().delete(response);
        then(tokenBlacklistService).should().register(accessToken, remainingSeconds);
    }

    @DisplayName("로그아웃 시 액세스 토큰이 없으면 UNAUTHORIZED를 던진다.")
    @Test
    void signOut_noAccessToken_throws() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(jwtTokenUtil.resolveToken(request)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.signOut(request, response))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }

    @DisplayName("Refresh Token 쿠키가 없으면 extractMemberIdFromRefreshCookie가 UNAUTHORIZED를 던진다.")
    @Test
    void extractMemberIdFromRefreshCookie_noRefreshToken_throws() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        given(refreshTokenCookieManager.read(request)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.extractMemberIdFromRefreshCookie(request))
                .isInstanceOf(GlobalException.class)
                .extracting("errorCode")
                .isEqualTo(GlobalErrorCode.UNAUTHORIZED);
    }
}
