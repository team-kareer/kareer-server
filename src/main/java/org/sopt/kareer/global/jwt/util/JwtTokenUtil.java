package org.sopt.kareer.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.config.jwt.JwtProperties;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.jwt.dto.TokenType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private final JwtProperties jwtProperties;

    public Long extractMemberId(String token, TokenType expectedType) {
        Claims claims = parseClaims(token);
        validateTokenType(claims, expectedType);
        return parseMemberId(claims);
    }

    public long extractRemainingValiditySeconds(String token, TokenType expectedType) {
        Claims claims = parseClaims(token);
        validateTokenType(claims, expectedType);
        return calculateRemainingSeconds(claims.getExpiration());
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authorizationHeader.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new GlobalException(GlobalErrorCode.JWT_EXPIRED);
        } catch (Exception ex) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID);
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void validateTokenType(Claims claims, TokenType expectedType) {
        String claimValue = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (claimValue == null || !expectedType.claimValue().equals(claimValue)) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID);
        }
    }

    private Long parseMemberId(Claims claims) {
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException ex) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID);
        }
    }

    private long calculateRemainingSeconds(Date expiration) {
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            throw new GlobalException(GlobalErrorCode.JWT_EXPIRED);
        }
        long remainingSeconds = remainingMillis / 1000;
        return Math.max(remainingSeconds, 1L);
    }
}
