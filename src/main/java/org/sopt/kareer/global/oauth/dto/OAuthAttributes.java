package org.sopt.kareer.global.oauth.dto;

import java.util.Map;
import org.sopt.kareer.domain.member.entity.OAuthProvider;

public record OAuthAttributes(
        OAuthProvider provider,
        String providerId,
        String email,
        String name,
        String picture,
        boolean emailVerified,
        Map<String, Object> attributes
) {

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());
        return new OAuthAttributes(
                provider,
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.getOrDefault("name", ""),
                (String) attributes.getOrDefault("picture", ""),
                Boolean.TRUE.equals(attributes.get("email_verified")),
                attributes
        );
    }
}
