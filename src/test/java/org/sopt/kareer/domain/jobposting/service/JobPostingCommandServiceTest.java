package org.sopt.kareer.domain.jobposting.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.global.external.ai.service.RagEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JobPostingCommandServiceTest {

    @Autowired
    private JobPostingCommandService jobPostingCommandService;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private JobPostingBookmarkRepository jobPostingBookmarkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RagEmbeddingService ragEmbeddingService;

    @AfterEach
    void tearDown() {
        jobPostingBookmarkRepository.deleteAllInBatch();
        jobPostingRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("채용공고 북마크를 생성한다.")
    @Test
    void createOrDeleteBookmark_create() {
        //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));

        //when
        jobPostingCommandService.createOrDeleteBookmark(member, jobPosting.getId());

        //then
        assertThat(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId())).isTrue();
    }

    @DisplayName("북마크가 이미 존재하면 삭제한다.")
    @Test
    void createOrDeleteBookmark_delete() {
        //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingCommandService.createOrDeleteBookmark(member, jobPosting.getId());

        //when
        jobPostingCommandService.createOrDeleteBookmark(member, jobPosting.getId());

        //then
        assertThat(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId())).isFalse();
    }

    @DisplayName("존재하지 않는 채용공고에 북마크를 추가하면 예외가 발생한다.")
    @Test
    void createOrDeleteBookmark_jobPostingNotFound() {
        //given
        Member member = memberRepository.save(MemberFixture.getMember());

        //when && then
        assertThatThrownBy(() -> jobPostingCommandService.createOrDeleteBookmark(member, 0L))
                .isInstanceOf(JobPostingException.class)
                .hasMessage(JobPostingErrorCode.JOB_POSTING_NOT_FOUND.getMessage());
    }
}
