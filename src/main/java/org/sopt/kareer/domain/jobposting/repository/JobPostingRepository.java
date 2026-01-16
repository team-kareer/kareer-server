package org.sopt.kareer.domain.jobposting.repository;


import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

}
