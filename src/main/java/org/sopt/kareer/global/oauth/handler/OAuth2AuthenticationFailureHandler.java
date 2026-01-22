package org.sopt.kareer.global.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.oauth.properties.OAuthRedirectProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OAuthRedirectProperties oAuthRedirectProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.warn("OAuth2 authentication failed", exception);

        String redirectUri = UriComponentsBuilder
                .fromUriString(oAuthRedirectProperties.redirectUri())
                .queryParam("error", "oauth_failure")
                .queryParam("error_description", "Authentication failed")
                .build()
                .encode()
                .toUriString();

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader(HttpHeaders.LOCATION, redirectUri);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Pragma", "no-cache");
    }
}
