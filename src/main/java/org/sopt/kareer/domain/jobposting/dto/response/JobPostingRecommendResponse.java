package org.sopt.kareer.domain.jobposting.dto.response;

import java.util.List;

public record JobPostingRecommendResponse(
        List<JobPostingResponse> jobPostingResponses
) {
}
