package org.sopt.kareer.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
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

    public List<JobPosting> getBookmarkedJobPostings(Long memberId) {
        return jobPostingBookmarkRepository
                .findAllByMemberId(memberId)
                .stream()
                .map(JobPostingBookmark::getJobPosting)
                .toList();
    }
}
