package org.sopt.kareer.domain.jobposting.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class JobPostingBookmarkRepositoryTest {

    @Autowired
    private JobPostingBookmarkRepository jobPostingBookmarkRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("jobPostingId와 memberId로 북마크 존재 여부를 판단할 수 있다.")
    @Test
    void existsByJobPostingIdAndMemberId(){
        //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPostingBookmark jobPostingBookmark = jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting));

       //when
        boolean result = jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId());

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("jobPostingId와 memberId로 북마크를 삭제할 수 있다.")
    @Test
    void deleteByJobPostingIdAndMemberId(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPostingBookmark jobPostingBookmark = jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting));

       //when
        jobPostingBookmarkRepository.deleteByJobPostingIdAndMemberId(jobPosting.getId(), member.getId());

       //then
        assertThat(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId())).isFalse();
    }

    @DisplayName("memberId로 존재하는 북마크들을 조회할 수 있다.")
    @Test
    void findAllByMemberId(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting1));

        JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting2));

        JobPosting jobPosting3 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting3));

       //when
        List<JobPosting> foundBookmarks = jobPostingBookmarkRepository.findAllByMemberId(member.getId());

        //then
        assertThat(foundBookmarks.size()).isEqualTo(3);
    }

    @DisplayName("memberId와 jobPostingId로 존재하는 북마크들을 조회할 수 있다.")
    @Test
    void findAllByMemberIdAndJobPostingId(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting1));

        JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting2));

        JobPosting jobPosting3 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingBookmarkRepository.save(JobPostingBookmark.create(member, jobPosting3));

       //when
        List<JobPostingBookmark> foundBookmarks = jobPostingBookmarkRepository
                .findAllByMemberIdAndJobPostingId(member.getId(), List.of(jobPosting1.getId(), jobPosting2.getId(), jobPosting3.getId()));

        //then
        assertThat(foundBookmarks.size()).isEqualTo(3);
    }
}
