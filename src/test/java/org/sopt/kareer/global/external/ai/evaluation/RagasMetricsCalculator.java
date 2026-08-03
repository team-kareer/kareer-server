package org.sopt.kareer.global.external.ai.evaluation;

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
 * RAGAS와 동등한 4개 지표를 Spring AI 내장 Evaluator({@link FactCheckingEvaluator}, {@link RelevancyEvaluator})만으로 계산한다.
 * 커스텀 judge 프롬프트는 두지 않고, 두 Evaluator의 기존 프롬프트를 각기 다른 용도로 재사용한다.
 */
public class RagasMetricsCalculator {

    private static final Logger log = LoggerFactory.getLogger(RagasMetricsCalculator.class);

    private final FactCheckingEvaluator factCheckingEvaluator;
    private final RelevancyEvaluator relevancyEvaluator;

    public RagasMetricsCalculator(ChatClient.Builder chatClientBuilder) {
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
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
}
