package org.sopt.kareer.global.external.discord.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.external.discord.dto.DiscordEmbedMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordClient {

    private final RestClient restClient;

    public void send(String uri, DiscordEmbedMessage message) {
        if (uri == null || uri.isBlank()) return;

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(message)
                    .retrieve()
                    .toEntity(String.class);

            log.info("[Discord Client] 메시지 발송 성공: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("[Discord Client] 메시지 전송 중 오류 발생: {}", e.getMessage());
        }
    }
}
