package org.sopt.kareer.global.auth.facade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberException;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacade {

    private final AuthService authService;
    private final MemberQueryService memberQueryService;
    private final LoginCodeService loginCodeService;

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        authService.signOut(request, response);
    }

    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        Long memberId = authService.extractMemberIdFromRefreshCookie(request);
        try {
            Member member = memberQueryService.getMemberById(memberId);
            JwtTokenDTO token = authService.generateToken(member, response);
            return TokenResponse.of(token);
        } catch (MemberException ex) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
        }
    }

    public TokenExchangeResponse exchange(TokenExchangeRequest request, HttpServletResponse response) {
        LoginCodePayload payload = loginCodeService.consume(request.code());
        try {
            Member member = memberQueryService.getMemberById(payload.memberId());
            JwtTokenDTO token = authService.generateToken(member, response);
            return new TokenExchangeResponse(token.accessToken(), payload.onboardingRequired());
        } catch (MemberException ex) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
        }
    }
}
