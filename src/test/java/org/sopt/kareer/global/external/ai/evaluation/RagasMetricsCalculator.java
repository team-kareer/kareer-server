package org.sopt.kareer.global.external.ai.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.RelevancyEvaluator;

import java.util.List;

/**
 * RAGAS와 동등한 4개 지표는 Spring AI 내장 Evaluator({@link FactCheckingEvaluator}, {@link RelevancyEvaluator})로 계산하고,
 * Kareer 도메인 전용 지표는 별도 judge 프롬프트로 계산한다.
 */
public class RagasMetricsCalculator {

    private static final Logger log = LoggerFactory.getLogger(RagasMetricsCalculator.class);

    private final FactCheckingEvaluator factCheckingEvaluator;
    private final RelevancyEvaluator relevancyEvaluator;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public RagasMetricsCalculator(ChatClient.Builder chatClientBuilder) {
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    /** Faithfulness: 생성된 답변(claim)이 검색된 context(document)로 지지되는가 */
    public float faithfulness(List<Document> context, String answerText) {
        EvaluationResponse response = factCheckingEvaluator.evaluate(new EvaluationRequest(context, answerText));
        return response.isPass() ? 1.0f : 0.0f;
    }

    /** Answer Relevancy: 생성된 답변이 query와 context에 비추어 관련성 있는가 */
    public float answerRelevancy(String query, List<Document> context, String answerText) {
        EvaluationResponse response = relevancyEvaluator.evaluate(new EvaluationRequest(query, context, answerText));
        return response.isPass() ? 1.0f : 0.0f;
    }

    /** Context Precision(reference-free): 검색된 청크 중 최종 답변과 부합하는 청크의 비율 */
    public float contextPrecision(String query, String answerText, List<Document> retrievedDocs) {
        if (retrievedDocs.isEmpty()) {
            log.warn("[RAGAS] contextPrecision called with empty retrievedDocs");
            return 0.0f;
        }

        long relevantCount = retrievedDocs.stream()
                .filter(doc -> relevancyEvaluator.evaluate(new EvaluationRequest(query, List.of(doc), answerText)).isPass())
                .count();

        return (float) relevantCount / retrievedDocs.size();
    }

    /** Context Recall(reference-based): 사람이 작성한 정답(referenceAnswer)이 검색된 문서들로 지지되는가 */
    public float contextRecall(String referenceAnswer, List<Document> retrievedDocs) {
        if (retrievedDocs.isEmpty()) {
            log.warn("[RAGAS] contextRecall called with empty retrievedDocs");
            return 0.0f;
        }

        EvaluationResponse response = factCheckingEvaluator.evaluate(new EvaluationRequest(retrievedDocs, referenceAnswer));
        return response.isPass() ? 1.0f : 0.0f;
    }

    /**
     * Job-Visa Path Coherence Score: 생성된 비자 전환 경로와 커리어 단계가 목표 직무 취업 경로로 현실적으로 맞물리는가.
     */
    public CustomMetricResult jobVisaPathCoherence(String userTargetJob, String generatedRoadmap) {
        String prompt = """
                아래 생성된 커리어 로드맵이 '%s' 직무를 목표로 하는
                외국인 유학생에게 비자 경로와 커리어 경로가 서로 일관되게 연결되어
                있는지 0.0~1.0 점수로 평가하세요.

                평가 기준 (기존 지표와 겹치지 않는 항목만):
                1. 제시된 비자 전환 순서(예: D-2->D-10->E-7)가 목표 직무 취업에
                   실제로 적용 가능한 경로인가 (0.5)
                   - 예: 마케팅 직무인데 E-9(비전문취업) 경로를 제시하면 낮은 점수
                2. 비자 전환 시점과 커리어 단계가 현실적으로 맞물리는가 (0.5)
                   - 예: D-10 기간(1년) 안에 취업 준비->E-7 전환까지의 흐름이
                     해당 직무 취업 난이도와 맞는가

                로드맵:
                %s

                JSON으로만 응답:
                {"score": 0.0~1.0, "reason": "한 줄 이유"}
                """.formatted(userTargetJob, generatedRoadmap);

        try {
            String responseJson = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            CustomMetricResult result = objectMapper.readValue(extractJsonObject(responseJson), CustomMetricResult.class);
            return new CustomMetricResult(clamp(result.score()), result.reason());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to evaluate Job-Visa Path Coherence Score", e);
        }
    }

    private String extractJsonObject(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("LLM response does not contain a JSON object: " + response);
        }
        return response.substring(start, end + 1);
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public record CustomMetricResult(double score, String reason) {
    }
}
