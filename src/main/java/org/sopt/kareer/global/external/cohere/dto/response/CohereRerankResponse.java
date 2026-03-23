package org.sopt.kareer.global.external.cohere.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CohereRerankResponse(
        String id,
        List<Result> results
) {
    public record Result(
            Integer index,
            @JsonProperty("relevance_score")
            Double relevanceScore
    ) {}
}
