package org.sopt.kareer.global.external.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.global.external.ai.dto.response.JobPostingRecommendResults;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.sopt.kareer.global.external.ai.exception.LlmErrorCode;
import org.sopt.kareer.global.external.ai.exception.LlmException;
import org.sopt.kareer.global.external.ai.prompt.JobPostingRecommendPrompt;
import org.sopt.kareer.global.external.ai.prompt.RoadmapPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {


    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    public RoadmapResponse generateRoadmap(
            String memberContext,
            List<Document> visaRequiredDocs,
            List<Document> careerRequiredDocs,
            List<Document> policyDocs
    ) {
        String visaContext = buildSectionContext("VISA", visaRequiredDocs);
        String careerContext = buildSectionContext("CAREER", careerRequiredDocs);
        String policyContext = buildSectionContext("POLICY", policyDocs);

        String systemPrompt = RoadmapPrompt.ROADMAP_SYSTEM_PROMPT;
        String userPrompt = RoadmapPrompt.ROADMAP_USER_PROMPT_FORMAT.formatted(
                LocalDate.now(),
                memberContext,
                visaContext,
                careerContext,
                policyContext
        );

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
                    sb.append("### chunk ").append(i);
                    sb.append(d.getText()).append("\n\n");
                    break;
                case JOBPOSTING:
                    sb.append("### candidate ").append(i);
                    Object jobPostingId = d.getMetadata().get("jobPostingId");
                    sb.append("jobPostingId=").append(jobPostingId);
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

    private String buildSectionContext(String section, List<Document> docs) {
        if (docs == null || docs.isEmpty()) return "EMPTY";

        StringBuilder sb = new StringBuilder();
        sb.append("SECTION=").append(section).append("\n");

        for (int i = 0; i < docs.size(); i++) {
            Document d = docs.get(i);

            String domain = Objects.toString(d.getMetadata().get(DOMAIN), "");
            String label = Objects.toString(d.getMetadata().get(LABEL), "");
            String title = Objects.toString(d.getMetadata().get(TITLE), "");

            sb.append("### doc ").append(i).append("\n");
            if (!domain.isBlank()) sb.append("domain=").append(domain).append("\n");
            if (!label.isBlank()) sb.append("label=").append(label).append("\n");
            if (!title.isBlank()) sb.append("title=").append(title).append("\n");
            sb.append(d.getText()).append("\n\n");
        }

        return sb.toString();
    }
}
