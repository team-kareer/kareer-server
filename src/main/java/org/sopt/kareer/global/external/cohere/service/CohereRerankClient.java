package org.sopt.kareer.global.external.cohere.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.external.cohere.dto.request.CohereRerankRequest;
import org.sopt.kareer.global.external.cohere.dto.response.CohereRerankResponse;
import org.sopt.kareer.global.external.cohere.properties.CohereProperties;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CohereRerankClient {

    private final CohereProperties cohereProperties;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(cohereProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cohereProperties.apiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<Document> rerank(String query, List<Document> documents, Integer topN) {
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        List<String> serializedDocs = documents.stream()
                .map(Document::getText)
                .toList();

        CohereRerankRequest request = new CohereRerankRequest(
                cohereProperties.rerank().model(),
                query,
                serializedDocs,
                topN != null
                        ? Math.min(topN, documents.size())
                        : Math.min(cohereProperties.rerank().topN(), documents.size())
        );

        try {
            CohereRerankResponse response = restClient()
                    .post()
                    .uri("/v2/rerank")
                    .body(request)
                    .retrieve()
                    .body(CohereRerankResponse.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                log.warn("Cohere rerank response empty. query={}", query);
                return documents;
            }

            List<Document> reranked = new ArrayList<>();
            for (CohereRerankResponse.Result result : response.results()) {
                reranked.add(documents.get(result.index()));
            }
            log.info("Cohere rerank results: {}", reranked);

            return reranked;

        } catch (Exception e) {
            log.error("Cohere rerank failed. fallback to original order. query={}", query, e);
            return documents;
        }
    }
}