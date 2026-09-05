package org.sopt.kareer.global.external.ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.global.external.ai.properties.RoadmapRagProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequiredDocumentRetrieverTest {

    @Mock
    private PgVectorStore requiredDocumentVectorStore;

    @Mock
    private EmbeddingModel embeddingModel;

    private static final double MIN_SIMILARITY = 0.15;

    private RequiredDocumentRetriever retriever() {
        RoadmapRagProperties props = new RoadmapRagProperties(4, 4, 4, 6, 30, MIN_SIMILARITY);
        return new RequiredDocumentRetriever(requiredDocumentVectorStore, props, embeddingModel);
    }

    private Member memberWithTargetJob(String targetJob) {
        return Member.builder()
                .name("test-user")
                .email("test@example.com")
                .provider(OAuthProvider.GOOGLE)
                .providerId("test-provider-id")
                .status(MemberStatus.ACTIVE)
                .targetJob(targetJob)
                .build();
    }

    private Document careerDocument(String domain) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requiredCategory", "CAREER");
        metadata.put("requiredDomain", domain);
        metadata.put("requiredLabel", "Action Required");
        return new Document("some career guidance text", metadata);
    }

    @DisplayName("targetJob이 후보 domain들과 임베딩 유사도가 임계값 미만이면 빈 목록을 반환한다.")
    @Test
    void retrieveCareer_returnsEmpty_whenTargetJobIsOutsideTaxonomy() {
        // given: "Sales"/"Production" 후보가 있지만, targetJob 임베딩이 둘 다와 유사도가 낮은 상황
        when(requiredDocumentVectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(careerDocument("Sales"), careerDocument("Production")));

        when(embeddingModel.embed("우주비행사")).thenReturn(new float[]{1f, 0f});
        when(embeddingModel.embed("Sales")).thenReturn(new float[]{0f, 1f});       // cosine similarity = 0
        when(embeddingModel.embed("Production")).thenReturn(new float[]{-1f, 0f}); // cosine similarity = -1

        Member member = memberWithTargetJob("우주비행사");

        // when
        RequiredDocumentRetriever.CareerSelectedDocs result = retriever().retrieveCareer(member);

        // then
        assertThat(result.actionRequired()).isEmpty();
        assertThat(result.aiGuideRisk()).isEmpty();
        assertThat(result.todoList()).isEmpty();
    }

    @DisplayName("targetJob이 후보 domain 중 하나와 임계값 이상으로 유사하면 해당 domain의 문서만 반환한다.")
    @Test
    void retrieveCareer_filtersToClosestDomain_whenSimilarityAboveThreshold() {
        // given: targetJob 임베딩이 "Sales"와 충분히 가깝고, "Production"과는 먼 상황
        when(requiredDocumentVectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(careerDocument("Sales"), careerDocument("Production")));

        when(embeddingModel.embed("영업 관리자")).thenReturn(new float[]{1f, 0f});
        when(embeddingModel.embed("Sales")).thenReturn(new float[]{0.9f, 0.436f});   // cosine similarity ≈ 0.9 (임계값 이상)
        when(embeddingModel.embed("Production")).thenReturn(new float[]{0f, 1f});     // cosine similarity = 0

        Member member = memberWithTargetJob("영업 관리자");

        // when
        RequiredDocumentRetriever.CareerSelectedDocs result = retriever().retrieveCareer(member);

        // then
        assertThat(result.actionRequired()).hasSize(1);
        assertThat(result.actionRequired().get(0).getMetadata().get("requiredDomain")).isEqualTo("Sales");
    }
}
