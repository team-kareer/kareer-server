package org.sopt.kareer.global.external.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.global.external.ai.constant.JobPostingRecommendPrompt;
import org.sopt.kareer.global.external.ai.constant.RoadmapPrompt;
import org.sopt.kareer.global.external.ai.dto.response.JobPostingRecommendResults;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.sopt.kareer.global.external.ai.exception.LlmErrorCode;
import org.sopt.kareer.global.external.ai.exception.LlmException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {


    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    public RoadmapResponse generateRoadmap(String memberContext, List<Document> retrievedDocument){
        String context = buildRagContext(retrievedDocument, RagType.DOCUMENT);

        String systemPrompt = RoadmapPrompt.ROADMAP_SYSTEM_PROMPT;

        String userPrompt = RoadmapPrompt.ROADMAP_USER_PROMPT_FORMAT.formatted(LocalDate.now(), memberContext, context);
        
        return call(systemPrompt, userPrompt, RoadmapResponse.class);
    }

    public List<Long> recommendJobPosting(String userContext, List<Document> retrievedDocument){
        String ragContext = buildRagContext(retrievedDocument, RagType.JOBPOSTING);

        String systemPrompt = JobPostingRecommendPrompt.JOB_POSTING_SYSTEM_PROMPT;
        String userPrompt = JobPostingRecommendPrompt.JOB_POSTING_USER_PROMPT_FORMAT.formatted(userContext, ragContext);

        JobPostingRecommendResults searched = call(systemPrompt, userPrompt, JobPostingRecommendResults.class);

        searched.results().forEach(i ->
                log.info("[JOB_RECOMMEND] jobPostingId={}, reason={}", i.jobPostingId(), i.reason()));

        return searched.results().stream()
                .map(JobPostingRecommendResults.JobPostingRecommendResult::jobPostingId)
                .toList();

    }

    private String buildRagContext(List<Document> docs, RagType ragType) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            Document d = docs.get(i);
            switch (ragType) {
                case DOCUMENT:
                    sb.append("### chunk ").append(i).append("\n");
                    sb.append(d.getText()).append("\n\n");
                    break;
                case JOBPOSTING:
                    sb.append("### candidate ").append(i).append("\n");
                    Object jobPostingId = d.getMetadata().get("jobPostingId");
                    sb.append("jobPostingId=").append(jobPostingId).append("\n");
                    sb.append(d.getText()).append("\n\n");

            }
        }
        return sb.toString();
    }

    private <T> T call(String systemPrompt, String userPrompt, Class<T> clazz) {
        try {
            ChatClient chatClient = chatClientBuilder.build();

            String responseJson = chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            return objectMapper.readValue(responseJson, clazz);
        } catch (Exception e) {
            throw new LlmException(LlmErrorCode.LLM_JSON_PARSING_FAILED, e.getMessage());
        }
    }
}
