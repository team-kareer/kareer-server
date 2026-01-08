package org.sopt.kareer.domain.jobposting.dto.response;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.util.List;

public record JobPostingCrawlListResponse(
        List<JobPostingCrawlResponse> crawlResponses
) {
    public static JobPostingCrawlListResponse of(List<JobPosting> jobPostings) {
        return new JobPostingCrawlListResponse(jobPostings.stream()
                .map(JobPostingCrawlResponse::from)
                .toList());
        }
    }
