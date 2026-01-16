package org.sopt.kareer.domain.jobposting.repository;

import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingBookmarkRepository extends JpaRepository<JobPostingBookmark, Long> {
    boolean existsByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    void deleteByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    List<JobPostingBookmark> findAllByMemberId(Long memberId);
}
