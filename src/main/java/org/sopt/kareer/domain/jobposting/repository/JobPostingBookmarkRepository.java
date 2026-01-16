package org.sopt.kareer.domain.jobposting.repository;

import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobPostingBookmarkRepository extends JpaRepository<JobPostingBookmark, Long> {
    boolean existsByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    void deleteByJobPostingIdAndMemberId(Long jobPostingId, Long memberId);

    @Query("""
        select jpb from JobPostingBookmark jpb where jpb.member.id = :memberId order by jpb.jobPosting.deadline asc
    """)
    List<JobPostingBookmark> findAllByMemberId(Long memberId);

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
}
