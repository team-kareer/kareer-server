package org.sopt.kareer.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClovaOcrWebClientConfig {

    @Bean
    public WebClient clovaOcrWebClient(
            WebClient.Builder builder,
            @Value("${spring.clova.ocr.url}") String baseUrl,
            @Value("${spring.clova.ocr.secret}") String secretKey
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-OCR-SECRET", secretKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
