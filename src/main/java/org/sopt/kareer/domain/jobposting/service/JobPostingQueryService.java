package org.sopt.kareer.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JobPostingQueryService {

    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;

    public JobPostingListResponse getJobPostingBookmarks(Long memberId) {
        List<JobPostingResponse> responses = jobPostingBookmarkRepository
                .findAllByMemberId(memberId)
                .stream()
                .map(JobPostingBookmark::getJobPosting)
                .map(jp -> JobPostingResponse.from(jp, true))
                .toList();

        return new JobPostingListResponse(responses);
    }
}
