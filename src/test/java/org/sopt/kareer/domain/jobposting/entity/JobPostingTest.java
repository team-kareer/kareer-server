package org.sopt.kareer.domain.jobposting.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class JobPostingTest {

    @DisplayName("JobPosting 객체를 생성할 수 있다.")
    @Test
    void createJobPosting(){
       //given
        LocalDate deadline = LocalDate.now();

        //when
        JobPosting jobPosting = JobPostingFixture.getJobPosting(1L, deadline);

       //then
        assertThat(jobPosting.getPostTitle()).isEqualTo(JobPostingFixture.POST_TITLE);
        assertThat(jobPosting.getDeadline()).isEqualTo(deadline);
        assertThat(jobPosting.getRecruitId()).isEqualTo(1L);
    }

}