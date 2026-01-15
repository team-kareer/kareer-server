package org.sopt.kareer.global.jwt;

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
import org.sopt.kareer.global.jwt.dto.JwtToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public JwtToken generate(Member member) {
        String accessToken = buildToken(member, jwtProperties.accessToken().expirationSeconds());
        String refreshToken = buildToken(member, jwtProperties.refreshToken().expirationSeconds());
        return new JwtToken(accessToken, refreshToken);
    }

    private String buildToken(Member member, long expirationSeconds) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiry = Date.from(now.plusSeconds(expirationSeconds));

        return Jwts.builder()
                .setSubject(String.valueOf(member.getId()))
                .addClaims(Map.of(
                        "provider", member.getProvider().name(),
                        "name", member.getName()
                ))
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
