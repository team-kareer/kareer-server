package org.sopt.kareer.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.dto.TokenResponse;
import org.sopt.kareer.global.jwt.JwtTokenProvider;
import org.sopt.kareer.global.jwt.dto.JwtToken;
import org.sopt.kareer.global.oauth.principal.CustomOAuth2User;
import org.sopt.kareer.domain.member.entity.MemberStatus;
import org.sopt.kareer.global.oauth.response.OAuth2LoginResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        JwtToken jwtToken = jwtTokenProvider.generate(principal.getMember());

        boolean onboardingRequired = principal.getStatus() == MemberStatus.PENDING;
        OAuth2LoginResponse body = new OAuth2LoginResponse(
                principal.getMember().getId(),
                onboardingRequired,
                TokenResponse.of(jwtToken)
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
