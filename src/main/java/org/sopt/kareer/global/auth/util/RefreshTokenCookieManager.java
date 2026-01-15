package org.sopt.kareer.global.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.config.RefreshTokenCookieProperties;
import org.sopt.kareer.global.config.jwt.JwtProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieManager {

    private final RefreshTokenCookieProperties refreshTokenCookieProperties;
    private final JwtProperties jwtProperties;

    public void write(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieProperties.name(), refreshToken)
                .httpOnly(true)
                .secure(refreshTokenCookieProperties.secure())
                .sameSite(refreshTokenCookieProperties.sameSite())
                .path("/")
                .maxAge(Duration.ofSeconds(jwtProperties.refreshToken().expirationSeconds()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public Optional<String> read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> refreshTokenCookieProperties.name().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

}
