package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagSearchService {

    private final PgVectorStore policyDocumentVectorStore;
    private final PgVectorStore jobPostingVectorStore;


    public List<Document> search(String query, int topK, RagType ragType) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return switch (ragType) {
            case DOCUMENT -> policyDocumentVectorStore.similaritySearch(request);
            case JOBPOSTING -> jobPostingVectorStore.similaritySearch(request);
        };
    }
}
