package org.sopt.kareer.global.external.cohere.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cohere")
public record CohereProperties(
        String apiKey,
        String baseUrl,
        Rerank rerank
) {
    public record Rerank(
            String model,
            int topN
    ) {}
}
