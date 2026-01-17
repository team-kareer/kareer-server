package org.sopt.kareer.global.external.ai.dto.request;

import java.util.List;

public record JobPostingEmbeddingRequest(
        List<Long> jobPostingIds
) {
}
