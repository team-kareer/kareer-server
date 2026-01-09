package org.sopt.kareer.domain.jobposting.dto.response;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.util.List;

public record JobPostingCrawlListResponse(
        List<JobPostingCrawlResponse> crawlResponses
) {
 
}
