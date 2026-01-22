package org.sopt.kareer.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class OnboardingRestrictionFilter extends OncePerRequestFilter {

    private static final List<String> ALWAYS_ALLOWED_PATHS = List.of(
            "/",
            "/api/v1/auth/**",
            "/api/v1/rag/**",
            "/actuator/health",
            "/h2-console/**",
            "/error",
            "/oauth2/**",
            "/login/oauth2/**",
            "/api/v1/job-postings/crawl",
            "/api/v1/members/roadmap/test",
            "/v3/api-docs/**"
    );

    private static final List<String> ONBOARD_ALLOWED_PATHS = List.of(
            "/api/v1/members/onboard/**"
    );

    private final MemberService memberService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return ALWAYS_ALLOWED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri))
                || ONBOARD_ALLOWED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long memberId)) {
            filterChain.doFilter(request, response);
            return;
        }

        Member member = memberService.getById(memberId);
        if (member.getStatus() == MemberStatus.PENDING) {
            throw new MemberException(MemberErrorCode.ONBOARDING_REQUIRED);
        }

        filterChain.doFilter(request, response);
    }
}
