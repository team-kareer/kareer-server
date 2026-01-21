package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
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
        String query = buildPolicyQuery(member, visa);

        return policyDocumentVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(props.policyTopK())
                        .build()
        );
    }

    private String buildPolicyQuery(Member member, MemberVisa visa) {
        return String.join(" | ",
                "Korea visa policy and employment rules",
                "visaType=" + (visa == null ? "" : visa.getVisaType().name()),
                "targetJob=" + nullSafe(member.getTargetJob()),
                "degree=" + (member.getDegree() == null ? "" : member.getDegree().name()),
                "graduation=" + (member.getExpectedGraduationDate() == null ? "" : member.getExpectedGraduationDate().toString())
        );
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }
}
