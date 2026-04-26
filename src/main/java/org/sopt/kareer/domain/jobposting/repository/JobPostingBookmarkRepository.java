package org.sopt.kareer.domain.jobposting.repository;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPostingBookmarkRepository extends JpaRepository<JobPostingBookmark, Long> {
    boolean existsByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    void deleteByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    @Query("""
    SELECT jp
    FROM JobPostingBookmark jpb
    JOIN FETCH jpb.jobPosting jp
    WHERE jpb.member.id = :memberId
    ORDER BY jp.deadline asc
""")
    List<JobPosting> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("""
    select jpb
    from JobPostingBookmark jpb
    where jpb.member.id = :memberId
      and jpb.jobPosting.id in :jobPostingIds
    order by jpb.jobPosting.deadline asc
""")
    List<JobPostingBookmark> findAllByMemberIdAndJobPostingId(
            Long memberId,
            List<Long> jobPostingIds
    );

    void deleteAllByMemberId(Long memberId);
}
