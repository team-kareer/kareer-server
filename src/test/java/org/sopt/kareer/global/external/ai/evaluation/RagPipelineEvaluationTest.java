package org.sopt.kareer.global.external.ai.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.service.dto.response.RoadmapGenerationContext;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.PolicyDocumentRetriever;
import org.sopt.kareer.global.external.ai.service.RequiredDocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Policy RAG / Required Document RAG의 검색 품질과 최종 로드맵 생성의 근거성을 평가하는
 * Spring AI 네이티브(RAGAS-equivalent) 평가 하네스.
 *
 * <p>실제 Postgres+pgvector+OpenAI를 호출하므로 {@code local} 프로파일(또는 그에 준하는 실 인프라 프로파일)로만 실행 가능하다.
 * 기본 {@code ./gradlew test}에서는 {@code ragas-eval} 태그가 제외되어 실행되지 않는다.
 *
 * <p>골든셋 케이스는 서로 완전히 독립적이므로(각자 다른 Member/MemberVisa, 다른 RoadmapGenerationContext),
 * 케이스마다 별도의 트랜잭션을 열어 virtual-thread executor로 동시에 실행한다. 케이스당 가장 오래 걸리는
 * 구간(로드맵 생성 자체)이 순차로 쌓이는 게 병목이었기 때문에, judge 호출 병렬화보다 이쪽이 실제 개선폭이 크다.
 * 각 케이스는 테스트 Member/MemberVisa 생성과 정리만 짧은 트랜잭션으로 처리한다. RAG 검색, 로드맵 생성,
 * RAGAS judge 호출은 OpenAI/Cohere 지연 중 DB 커넥션을 점유하지 않도록 트랜잭션 밖에서 실행한다.
 *
 * <pre>{@code ./gradlew ragasEval -Dspring.profiles.active=local}</pre>
 */
@Tag("ragas-eval")
@SpringBootTest
@ActiveProfiles("local")
class RagPipelineEvaluationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberVisaRepository memberVisaRepository;

    @Autowired
    private MemberContextBuilder memberContextBuilder;

    @Autowired
    private RequiredDocumentRetriever requiredDocumentRetriever;

    @Autowired
    private PolicyDocumentRetriever policyDocumentRetriever;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void evaluateRagPipelines() throws Exception {
        List<GoldenCase> goldenCases = new GoldenSetLoader(objectMapper).load();
        RagasMetricsCalculator metrics = new RagasMetricsCalculator(chatClientBuilder);
        RagasReportPrinter printer = new RagasReportPrinter(objectMapper);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        List<CompletableFuture<CaseResult>> futures = goldenCases.stream()
                .map(goldenCase -> CompletableFuture.supplyAsync(
                        () -> evaluateCase(goldenCase, metrics, transactionTemplate),
                        executorService
                ))
                .toList();

        for (CompletableFuture<CaseResult> future : futures) {
            CaseResult outcome = future.join();
            printer.recordCase(
                    outcome.caseId(),
                    outcome.policyPrecision(),
                    outcome.policyRecall(),
                    outcome.requiredDocPrecision(),
                    outcome.requiredDocRecall(),
                    outcome.jobVisaPathCoherence(),
                    outcome.jobVisaPathCoherenceReason(),
                    outcome.phaseResults()
            );
        }

        printer.printReport();
        printer.writeHtmlReport(Path.of("build/reports/ragas/index.html"));
    }

    private CaseResult evaluateCase(
            GoldenCase goldenCase,
            RagasMetricsCalculator metrics,
            TransactionTemplate transactionTemplate
    ) {
        EvaluationSubject subject = createEvaluationSubject(goldenCase, transactionTemplate);
        try {
            RoadmapGenerationContext context = buildContext(subject);
            RoadmapResponse roadmap = openAiService.generateRoadmap(
                    context.memberContextText(),
                    context.visaDocs(),
                    context.careerDocs(),
                    context.policyDocs()
            );

            String answerText = writeValueAsStringUnchecked(roadmap);
            String query = context.memberContextText();

            List<Document> requiredDocumentDocs = new ArrayList<>();
            requiredDocumentDocs.addAll(context.visaDocs());
            requiredDocumentDocs.addAll(context.careerDocs());

            // Phase에 대한 평가를 추가로 진행한다.
            List<PhaseResult> phaseResults = new ArrayList<>();
            for (RoadmapResponse.PhasePlan phase : roadmap.phases()) {
                String phaseText = writeValueAsStringUnchecked(phase);
                phaseResults.add(new PhaseResult(
                        phase.sequence(),
                        phase.goal(),
                        metrics.faithfulness(context.allDocs(), phaseText),
                        metrics.answerRelevancy(query, context.allDocs(), phaseText)
                ));
            }

            return new CaseResult(
                    goldenCase.caseId(),
                    metrics.contextPrecision(query, answerText, context.policyDocs()),
                    metrics.contextRecall(goldenCase.referenceAnswer(), context.policyDocs()),
                    metrics.contextPrecision(query, answerText, requiredDocumentDocs),
                    metrics.contextRecall(goldenCase.referenceAnswer(), requiredDocumentDocs),
                    metrics.jobVisaPathCoherence(goldenCase.targetJob(), answerText),
                    phaseResults
            );
        } finally {
            cleanupEvaluationSubject(subject.memberId(), transactionTemplate);
        }
    }

    private EvaluationSubject createEvaluationSubject(GoldenCase goldenCase, TransactionTemplate transactionTemplate) {
        return transactionTemplate.execute(status -> {
            String uniqueSuffix = goldenCase.caseId() + "-" + UUID.randomUUID();
            Member member = memberRepository.save(GoldenCaseMemberFactory.toMember(goldenCase, uniqueSuffix));
            MemberVisa visa = memberVisaRepository.save(GoldenCaseMemberFactory.toVisa(member, goldenCase));
            return new EvaluationSubject(member.getId(), visa);
        });
    }

    private RoadmapGenerationContext buildContext(EvaluationSubject subject) {
        MemberContextBuilder.MemberAndContext memberContext = memberContextBuilder.load(subject.memberId());

        List<Document> visaDocs = requiredDocumentRetriever.retrieveVisaAll(subject.visa());

        RequiredDocumentRetriever.CareerSelectedDocs careerSelected = requiredDocumentRetriever.retrieveCareer(memberContext.member());
        List<Document> careerDocs = new ArrayList<>();
        careerDocs.addAll(careerSelected.actionRequired());
        careerDocs.addAll(careerSelected.aiGuideRisk());
        careerDocs.addAll(careerSelected.todoList());

        List<Document> policyDocs = policyDocumentRetriever.retrievePolicy(memberContext.member(), subject.visa());

        return new RoadmapGenerationContext(memberContext.contextText(), visaDocs, careerDocs, policyDocs);
    }

    private void cleanupEvaluationSubject(Long memberId, TransactionTemplate transactionTemplate) {
        transactionTemplate.executeWithoutResult(status -> {
            memberVisaRepository.deleteAllByMemberId(memberId);
            memberRepository.deleteById(memberId);
        });
    }

    private String writeValueAsStringUnchecked(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize " + value, e);
        }
    }

    private record CaseResult(
            String caseId,
            double policyPrecision,
            double policyRecall,
            double requiredDocPrecision,
            double requiredDocRecall,
            RagasMetricsCalculator.CustomMetricResult jobVisaPathCoherenceResult,
            List<PhaseResult> phaseResults
    ) {
        double jobVisaPathCoherence() {
            return jobVisaPathCoherenceResult.score();
        }

        String jobVisaPathCoherenceReason() {
            return jobVisaPathCoherenceResult.reason();
        }
    }

    private record EvaluationSubject(Long memberId, MemberVisa visa) {
    }
}
