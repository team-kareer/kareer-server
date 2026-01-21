package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.global.external.ai.builder.query.PolicyQueryBuilder;
import org.sopt.kareer.global.external.ai.properties.RoadmapRagProperties;
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

    public List<Document> retrievePolicy(Member member, MemberVisa visa) {
        String query = PolicyQueryBuilder.buildPolicyQuery(member, visa);

        return policyDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(props.policyTopK())
                        .build()
        );
    }

}
