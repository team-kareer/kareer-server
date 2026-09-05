package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.global.external.ai.enums.RequiredDepth;
import org.sopt.kareer.global.external.ai.properties.RoadmapRagProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.DOMAIN;
import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequiredDocumentRetriever {

    private final PgVectorStore requiredDocumentVectorStore;
    private final RoadmapRagProperties props;
    private final EmbeddingModel embeddingModel;

    public List<Document> retrieveVisaAll(MemberVisa visa) {
        String domain = visa.getVisaType().getDescription();

        List<Document> candidates = requiredDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Visa requirements for " + domain)
                        .topK(props.candidatePoolTopK())
                        .filterExpression("requiredCategory == 'VISA'")
                        .build()
        );

        return candidates.stream()
                .filter(d -> equalDomain(d, domain))
                .toList();

    }

    public CareerSelectedDocs retrieveCareer(Member member) {
        String targetJob = member.getTargetJob();

        List<Document> candidates = requiredDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(targetJob + " visa extension requirements checklist required documents process")
                        .topK(props.candidatePoolTopK())
                        .filterExpression("requiredCategory == 'CAREER'")
                        .build()
        );

        List<Document> domainMatched = filterByClosestDomain(candidates, targetJob);

        return new CareerSelectedDocs(
                takeByDepth(domainMatched, RequiredDepth.D1, props.careerActionTopK()),
                takeByDepth(domainMatched, RequiredDepth.D2_1, props.careerGuideTopK()),
                takeByDepth(domainMatched, RequiredDepth.D2_2, props.careerTodoTopK())
        );
    }

    private boolean equalDomain(Document d, String domain) {
        return domain.equalsIgnoreCase(
                Objects.toString(d.getMetadata().get(DOMAIN), "")
        );
    }

    /**
     * targetJob은 회원이 자유 텍스트(주로 한국어)로 입력하는 값이라, 임베딩된 문서의 domain
     * 라벨(영문 카테고리 문자열)과 문자열이 정확히 일치하는 경우가 거의 없다. candidates 안에
     * 실제로 존재하는 domain들 중 targetJob과 임베딩 코사인 유사도가 가장 높은 domain 하나를 골라
     * 그 domain에 속한 문서만 사용한다.
     */
    private List<Document> filterByClosestDomain(List<Document> candidates, String targetJob) {
        List<String> availableDomains = candidates.stream()
                .map(d -> Objects.toString(d.getMetadata().get(DOMAIN), ""))
                .filter(domain -> !domain.isBlank())
                .distinct()
                .toList();

        if (availableDomains.isEmpty()) {
            return List.of();
        }

        float[] targetJobEmbedding = embeddingModel.embed(targetJob);

        Map<String, Double> similarityByDomain = availableDomains.stream()
                .collect(Collectors.toMap(
                        domain -> domain,
                        domain -> cosineSimilarity(targetJobEmbedding, embeddingModel.embed(domain))
                ));

        Map.Entry<String, Double> closest = similarityByDomain.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        if (closest.getValue() < props.careerDomainMinSimilarity()) {
            log.warn("[REQUIRED_DOCUMENT] targetJob=\"{}\" is below similarity threshold {} (best match: {}={}) — treating as unclassified",
                    targetJob, props.careerDomainMinSimilarity(), closest.getKey(), closest.getValue());
            return List.of();
        }

        log.info("[REQUIRED_DOCUMENT] targetJob=\"{}\" matched to domain=\"{}\" (candidates: {})",
                targetJob, closest.getKey(), similarityByDomain);

        return candidates.stream()
                .filter(d -> closest.getKey().equalsIgnoreCase(Objects.toString(d.getMetadata().get(DOMAIN), "")))
                .toList();
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<Document> takeByDepth(
            List<Document> docs,
            RequiredDepth depth,
            int limit
    ) {
        return dedupeByChunkId(
                docs.stream()
                        .filter(d -> depth.getLabel().equals(
                                Objects.toString(d.getMetadata().get(LABEL), "")
                        ))
                        .limit(limit)
                        .toList()
        );
    }

    private List<Document> dedupeByChunkId(List<Document> docs) {
        Map<String, Document> map = new LinkedHashMap<>();
        for (Document d : docs) {
            String key = Objects.toString(d.getMetadata().get("chunkId"), null);
            if (key == null) key = Integer.toHexString(d.getText().hashCode());
            map.putIfAbsent(key, d);
        }
        return new ArrayList<>(map.values());
    }

    public record CareerSelectedDocs(
            List<Document> actionRequired,
            List<Document> aiGuideRisk,
            List<Document> todoList
    ) {}
}