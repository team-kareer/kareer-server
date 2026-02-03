package org.sopt.kareer.domain.jobposting.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
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
class JobPostingServiceTest {

    @Autowired
    private JobPostingService jobPostingService;

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

    @DisplayName("채용공고 북마크를 생성할 수 있다.")
    @Test
    void createOrDeleteBookmark(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));

       //when
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting.getId());

       //then
        boolean result = jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId());
        assertThat(result).isTrue();

    }

    @DisplayName("채용공고 북마크가 이미 존재하면 북마크를 삭제한다.")
    @Test
    void deleteBookmark(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting.getId());

       //when
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting.getId());

       //then
        boolean result = jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId());
        assertThat(result).isFalse();
    }

    @DisplayName("존재하지 않는 채용공고에 대해 북마크를 추가하려고 하면 예외가 발생한다.")
    @Test
    void createOrDeleteBookmarkWithoutJobPosting(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());

        //when && then
        assertThatThrownBy(() -> jobPostingService.createOrDeleteBookmark(member.getId(), 1L))
                .hasMessage(JobPostingErrorCode.JOB_POSTING_NOT_FOUND.getMessage());

    }

    @DisplayName("북마크에 추가한 채용공고들을 조회한다.")
    @Test
    void getJobPostingBookmarks(){
       //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPosting jobPosting3 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting1.getId());
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting2.getId());
        jobPostingService.createOrDeleteBookmark(member.getId(), jobPosting3.getId());

        //when
        JobPostingListResponse response = jobPostingService.getJobPostingBookmarks(member.getId());

        //then
        assertThat(response.jobPostingResponses()).hasSize(3);
    }



}