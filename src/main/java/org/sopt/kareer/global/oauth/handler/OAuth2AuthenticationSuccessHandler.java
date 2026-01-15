package org.sopt.kareer.global.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.global.auth.service.LoginCodeService;
import org.sopt.kareer.global.oauth.properties.OAuthRedirectProperties;
import org.sopt.kareer.global.oauth.principal.CustomOAuth2User;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginCodeService loginCodeService;
    private final OAuthRedirectProperties oAuthRedirectProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        boolean onboardingRequired = principal.getStatus() == MemberStatus.PENDING;
        String code = loginCodeService.issue(principal.getMember(), onboardingRequired);

        String redirectUri = UriComponentsBuilder
                .fromUriString(oAuthRedirectProperties.redirectUri())
                .queryParam("code", code)
                .build(true)
                .toUriString();

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader(HttpHeaders.LOCATION, redirectUri);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Pragma", "no-cache");
    }
}
