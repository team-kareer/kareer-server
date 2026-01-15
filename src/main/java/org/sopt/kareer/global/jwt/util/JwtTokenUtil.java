package org.sopt.kareer.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.config.jwt.JwtProperties;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    public void validateToken(String token) {
        parseClaims(token);
    }

    public Long extractMemberId(String token) {
        try {
            return Long.parseLong(parseClaims(token).getSubject());
        } catch (NumberFormatException ex) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID);
        }
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
}
