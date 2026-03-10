package org.sopt.kareer.global.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberService;
    private final LoginCodeService loginCodeService;
    private final RefreshTokenCookieManager refreshTokenCookieManager;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String BEARER_PREFIX = "Bearer ";

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenCookieManager.delete(response);
        String accessToken = resolveAccessToken(request);
        long remainingSeconds = jwtTokenUtil.extractRemainingValiditySeconds(accessToken, TokenType.ACCESS);
        tokenBlacklistService.register(accessToken, remainingSeconds);
    }

    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = refreshTokenCookieManager.read(request)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.UNAUTHORIZED));
        Long memberId = jwtTokenUtil.extractMemberId(refreshToken, TokenType.REFRESH);

        try {
            Member member = memberService.getById(memberId);
            JwtTokenDTO newToken = jwtTokenProvider.generate(member);
            refreshTokenCookieManager.write(response, newToken.refreshToken());
            return TokenResponse.of(newToken);
        } catch (MemberException ex) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
        }
    }

    public TokenExchangeResponse exchange(TokenExchangeRequest request, HttpServletResponse response) {
        LoginCodePayload payload = loginCodeService.consume(request.code());

        try {
            Member member = memberService.getById(payload.memberId());
            JwtTokenDTO newToken = jwtTokenProvider.generate(member);
            refreshTokenCookieManager.write(response, newToken.refreshToken());
            return new TokenExchangeResponse(newToken.accessToken(), payload.onboardingRequired());
        } catch (MemberException ex) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
        }
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
    }
}
