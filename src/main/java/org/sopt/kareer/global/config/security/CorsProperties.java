package org.sopt.kareer.global.config.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        String allowedOrigins,
        boolean allowCredentials
) {

    public List<String> allowedOriginList() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            return List.of();
        }
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());
    }
}
