package org.sopt.kareer.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.config.jwt.JwtProperties;
import org.sopt.kareer.global.exception.customexception.UnauthorizedException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    public void validateAccessToken(String token) {
        parseClaims(token, jwtProperties.accessToken());
    }

    public void validateRefreshToken(String token) {
        parseClaims(token, jwtProperties.refreshToken());
    }

    public Long extractMemberId(String token) {
        return Long.parseLong(parseClaims(token, jwtProperties.accessToken()).getSubject());
    }

    private Claims parseClaims(String token, JwtProperties.TokenProperties tokenProperties) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(tokenProperties))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException(GlobalErrorCode.JWT_EXPIRED);
        } catch (Exception ex) {
            throw new UnauthorizedException(GlobalErrorCode.JWT_INVALID);
        }
    }

    private Key getSigningKey(JwtProperties.TokenProperties tokenProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(tokenProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
