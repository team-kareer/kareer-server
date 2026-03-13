package org.sopt.kareer.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.auth.service.TokenBlacklistService;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.dto.TokenType;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberService;
    private final Optional<TokenBlacklistService> tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenUtil.resolveToken(request).orElse(null);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            tokenBlacklistService.ifPresent(service -> {
                if (service.contains(token)) {
                    throw new GlobalException(GlobalErrorCode.JWT_INVALID);
                }
            });
            try {
                Long memberId = jwtTokenUtil.extractMemberId(token, TokenType.ACCESS);
                Member member = memberService.getById(memberId);
                setAuthentication(request, member);
            } catch (MemberException ex) {
                throw new GlobalException(GlobalErrorCode.UNAUTHORIZED);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, Member member) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                member.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
