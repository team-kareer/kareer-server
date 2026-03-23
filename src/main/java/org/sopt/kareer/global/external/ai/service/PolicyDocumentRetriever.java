package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.global.external.ai.builder.query.PolicyQueryBuilder;
import org.sopt.kareer.global.external.ai.properties.RoadmapRagProperties;
import org.sopt.kareer.global.external.cohere.service.CohereRerankClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PolicyDocumentRetriever {

    private final PgVectorStore policyDocumentVectorStore;
    private final RoadmapRagProperties props;
    private final CohereRerankClient cohereRerankClient;

    public List<Document> retrievePolicy(Member member, MemberVisa visa) {
        String query = PolicyQueryBuilder.buildPolicyQuery(member, visa);

        List<Document> candidates = policyDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(props.candidatePoolTopK())
                        .build()
        );

        return cohereRerankClient.rerank(query, candidates, props.policyTopK());
    }

}
