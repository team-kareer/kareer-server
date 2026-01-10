package org.sopt.kareer.domain.jobposting.repository;


import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    boolean existsByRecruitId(Long recruitId);

    @Query("select j.recruitId from JobPosting j where j.recruitId in :recruitIds")
    List<Long> findExistingRecruitIds(@Param("recruitIds") Collection<Long> recruitIds);
}
