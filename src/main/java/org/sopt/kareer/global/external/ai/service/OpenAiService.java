package org.sopt.kareer.global.external.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.external.ai.constant.OpenAiPrompt;
import org.sopt.kareer.domain.member.dto.response.RoadmapResponse;
import org.sopt.kareer.global.external.ai.exception.AiErrorCode;
import org.sopt.kareer.global.external.ai.exception.AiException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public RoadmapResponse generateRoadmap(String memberContext, List<Document> retrievedDocument){
        String context = buildRagContext(retrievedDocument);

        String systemPrompt = OpenAiPrompt.ROADMAP_SYSTEM_PROMPT;

        String userPrompt = OpenAiPrompt.ROADMAP_USER_PROMPT_FORMAT.formatted(LocalDate.now(), memberContext, context);

        String responseJson = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        try {
            return objectMapper.readValue(responseJson, RoadmapResponse.class);
        } catch (Exception e) {
            throw new AiException(AiErrorCode.LLM_JSON_PARSING_FAILED, e.getMessage());
        }
    }

    private String buildRagContext(List<Document> docs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            Document d = docs.get(i);
            sb.append("### chunk ").append(i).append("\n");
            sb.append(d.getText()).append("\n\n");
        }
        return sb.toString();
    }
}
