package org.sopt.kareer.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JobPostingCommandService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;

    public void createOrDeleteBookmark(Member member, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new JobPostingException(JobPostingErrorCode.JOB_POSTING_NOT_FOUND));

        if (jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPostingId, member.getId())) {
            jobPostingBookmarkRepository.deleteByJobPostingIdAndMemberId(jobPostingId, member.getId());
            return;
        }

        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting));
    }
}
