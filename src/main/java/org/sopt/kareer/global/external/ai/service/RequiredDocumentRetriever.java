package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.global.external.ai.enums.RequiredDepth;
import org.sopt.kareer.global.external.ai.properties.RoadmapRagProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.DOMAIN;
import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequiredDocumentRetriever {

    private final PgVectorStore requiredDocumentVectorStore;
    private final RoadmapRagProperties props;

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
        String domain = member.getTargetJob();

        List<Document> candidates = requiredDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(domain + " visa extension requirements checklist required documents process")
                        .topK(props.candidatePoolTopK())
                        .filterExpression("requiredCategory == 'CAREER'")
                        .build()
        );

        List<Document> domainMatched = candidates.stream()
                .filter(d -> equalDomain(d, domain))
                .toList();

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