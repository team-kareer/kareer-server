package org.sopt.kareer.global.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.exception.customexception.NotFoundException;
import org.sopt.kareer.global.exception.customexception.UnauthorizedException;
import org.sopt.kareer.global.exception.errorcode.AuthErrorCode;
import org.sopt.kareer.global.auth.dto.TokenReissueRequest;
import org.sopt.kareer.global.auth.dto.TokenResponse;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.dto.JwtToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public TokenResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.refreshToken();
        jwtTokenProvider.validateRefreshToken(refreshToken);
        Long memberId = jwtTokenProvider.extractMemberIdFromRefreshToken(refreshToken);

        try {
            Member member = memberService.getById(memberId);
            JwtToken newToken = jwtTokenProvider.generate(member);
            return TokenResponse.of(newToken);
        } catch (NotFoundException ex) {
            throw new UnauthorizedException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}
