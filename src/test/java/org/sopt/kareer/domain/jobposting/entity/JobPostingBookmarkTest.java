package org.sopt.kareer.domain.jobposting.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class JobPostingBookmarkTest {

    @DisplayName("JobPostingBookmark 객체를 생성할 수 있다.")
    @Test
    void createJobPostingBookmark() {
       //given
        Member member = MemberFixture.getMember();
        JobPosting jobPosting = JobPostingFixture.getJobPosting(LocalDate.now());

       //when
        JobPostingBookmark jobPostingBookmark = JobPostingBookmark.create(member, jobPosting);

        //then
        assertThat(jobPostingBookmark.getJobPosting()).isEqualTo(jobPosting);
        assertThat(jobPostingBookmark.getMember()).isEqualTo(member);
    }

}