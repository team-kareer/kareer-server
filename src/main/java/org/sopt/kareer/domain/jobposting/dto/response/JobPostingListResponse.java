package org.sopt.kareer.domain.jobposting.dto.response;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.util.List;

public record JobPostingListResponse(
        List<JobPostingResponse> jobPostingResponses
) {
    public static JobPostingListResponse from(List<JobPosting>  jobPostings) {
        return new JobPostingListResponse(jobPostings.stream()
                .map(JobPostingResponse::from)
                .toList());
    }
}
