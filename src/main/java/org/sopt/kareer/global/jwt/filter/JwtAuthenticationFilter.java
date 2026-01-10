package org.sopt.kareer.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.exception.customexception.NotFoundException;
import org.sopt.kareer.global.exception.customexception.UnauthorizedException;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.exception.errorcode.MemberErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                jwtTokenProvider.validateAccessToken(token);
                Long memberId = jwtTokenProvider.extractMemberIdFromAccessToken(token);
                Member member = memberService.getById(memberId);
                setAuthentication(request, member);
            } catch (NotFoundException ex) {
                throw new UnauthorizedException(MemberErrorCode.MEMBER_NOT_FOUND);
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

    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
