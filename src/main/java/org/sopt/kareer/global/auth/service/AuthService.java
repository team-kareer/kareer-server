package org.sopt.kareer.global.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.global.auth.util.RefreshTokenCookieManager;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.dto.JwtTokenDTO;
import org.sopt.kareer.global.jwt.dto.TokenType;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenCookieManager refreshTokenCookieManager;
    private final TokenBlacklistService tokenBlacklistService;

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenCookieManager.delete(response);
        String accessToken = jwtTokenUtil.resolveToken(request)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.UNAUTHORIZED));

        try {
            long remainingSeconds = jwtTokenUtil.extractRemainingValiditySeconds(accessToken, TokenType.ACCESS);
            tokenBlacklistService.register(accessToken, remainingSeconds);
        } catch (GlobalException ex) {
            if (ex.getErrorCode() != GlobalErrorCode.JWT_EXPIRED) {
                throw ex;
            }
        }
    }

    public Long extractMemberIdFromRefreshCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenCookieManager.read(request)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.UNAUTHORIZED));
        return jwtTokenUtil.extractMemberId(refreshToken, TokenType.REFRESH);
    }

    public JwtTokenDTO generateToken(Member member, HttpServletResponse response) {
        JwtTokenDTO token = jwtTokenProvider.generate(member);
        refreshTokenCookieManager.write(response, token.refreshToken());
        return token;
    }
}
