package org.sopt.kareer.global.auth.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.auth.dto.TokenReissueRequest;
import org.sopt.kareer.global.auth.dto.TokenResponse;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.sopt.kareer.global.jwt.dto.JwtToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public TokenResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.refreshToken();
        jwtTokenUtil.validateRefreshToken(refreshToken);
        Long memberId = jwtTokenUtil.extractMemberId(refreshToken);

        try {
            Member member = memberService.getById(memberId);
            JwtToken newToken = jwtTokenProvider.generate(member);
            return TokenResponse.of(newToken);
        } catch (MemberException ex) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
        }
    }
}
