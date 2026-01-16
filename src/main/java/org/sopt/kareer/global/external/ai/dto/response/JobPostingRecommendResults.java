package org.sopt.kareer.global.external.ai.dto.response;

import java.util.List;

public record JobPostingRecommendResults(
        List<JobPostingRecommendResult> results
) {

    public record JobPostingRecommendResult(
            Long jobPostingId,
            String reason
    ){}
}
