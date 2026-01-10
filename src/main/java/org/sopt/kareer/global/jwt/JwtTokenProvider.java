package org.sopt.kareer.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.global.config.jwt.JwtProperties;
import org.sopt.kareer.global.exception.customexception.UnauthorizedException;
import org.sopt.kareer.global.exception.errorcode.AuthErrorCode;
import org.sopt.kareer.global.jwt.dto.JwtToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public JwtToken generate(Member member) {
        String accessToken = buildToken(member, jwtProperties.accessToken());
        String refreshToken = buildToken(member, jwtProperties.refreshToken());
        return new JwtToken(accessToken, refreshToken);
    }

    public void validateAccessToken(String token) {
        parseClaims(token, jwtProperties.accessToken());
    }

    public void validateRefreshToken(String token) {
        parseClaims(token, jwtProperties.refreshToken());
    }

    public Long extractMemberIdFromAccessToken(String token) {
        return Long.parseLong(parseClaims(token, jwtProperties.accessToken()).getSubject());
    }

    public Long extractMemberIdFromRefreshToken(String token) {
        return Long.parseLong(parseClaims(token, jwtProperties.refreshToken()).getSubject());
    }

    public LocalDateTime getRefreshTokenExpiry() {
        Instant now = Instant.now();
        long seconds = jwtProperties.refreshToken().expirationSeconds();
        return LocalDateTime.ofInstant(now.plusSeconds(seconds), ZoneId.systemDefault());
    }

    private String buildToken(Member member, JwtProperties.TokenProperties tokenProperties) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiry = Date.from(now.plusSeconds(tokenProperties.expirationSeconds()));

        return Jwts.builder()
                .setSubject(String.valueOf(member.getId()))
                .addClaims(Map.of(
                        "provider", member.getProvider().name(),
                        "name", member.getName()
                ))
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(getSigningKey(tokenProperties), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token, JwtProperties.TokenProperties tokenProperties) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(tokenProperties))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException(AuthErrorCode.JWT_EXPIRED);
        } catch (Exception ex) {
            throw new UnauthorizedException(AuthErrorCode.JWT_INVALID);
        }
    }

    private Key getSigningKey(JwtProperties.TokenProperties tokenProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(tokenProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
