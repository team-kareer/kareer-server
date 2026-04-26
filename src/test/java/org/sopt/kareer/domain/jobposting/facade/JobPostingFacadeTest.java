package org.sopt.kareer.domain.jobposting.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
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
class JobPostingFacadeTest {

    @Autowired
    private JobPostingFacade jobPostingFacade;

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

    @Nested
    @DisplayName("북마크 채용공고를 조회한다.")
    class GetJobPostingBookmarks {

        @Test
        @DisplayName("북마크한 채용공고 목록이 반환된다.")
        void getJobPostingBookmarks_success() {
            //given
            Member member = memberRepository.save(MemberFixture.getMember());
            JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
            JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now().plusDays(1)));
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting1.getId());
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting2.getId());

            //when
            JobPostingListResponse response = jobPostingFacade.getJobPostingBookmarks(member.getId());

            //then
            assertThat(response.jobPostingResponses()).hasSize(2);
        }

        @Test
        @DisplayName("반환된 채용공고는 모두 isBookmarked가 true이다.")
        void getJobPostingBookmarks_allBookmarked() {
            //given
            Member member = memberRepository.save(MemberFixture.getMember());
            JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting.getId());

            //when
            JobPostingListResponse response = jobPostingFacade.getJobPostingBookmarks(member.getId());

            //then
            assertThat(response.jobPostingResponses())
                    .allMatch(r -> r.isBookmarked());
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 조회하면 예외가 발생한다.")
        void getJobPostingBookmarks_memberNotFound() {
            //when && then
            assertThatThrownBy(() -> jobPostingFacade.getJobPostingBookmarks(0L))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("채용공고 북마크를 추가/삭제한다.")
    class CreateOrDeleteBookmark {

        @Test
        @DisplayName("북마크가 없으면 생성한다.")
        void createOrDeleteBookmark_create() {
            //given
            Member member = memberRepository.save(MemberFixture.getMember());
            JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));

            //when
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting.getId());

            //then
            assertThat(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId())).isTrue();
        }

        @Test
        @DisplayName("북마크가 이미 있으면 삭제한다.")
        void createOrDeleteBookmark_delete() {
            //given
            Member member = memberRepository.save(MemberFixture.getMember());
            JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting.getId());

            //when
            jobPostingFacade.createOrDeleteBookmark(member.getId(), jobPosting.getId());

            //then
            assertThat(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPosting.getId(), member.getId())).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 예외가 발생한다.")
        void createOrDeleteBookmark_memberNotFound() {
            //given
            JobPosting jobPosting = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));

            //when && then
            assertThatThrownBy(() -> jobPostingFacade.createOrDeleteBookmark(0L, jobPosting.getId()))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 채용공고이면 예외가 발생한다.")
        void createOrDeleteBookmark_jobPostingNotFound() {
            //given
            Member member = memberRepository.save(MemberFixture.getMember());

            //when && then
            assertThatThrownBy(() -> jobPostingFacade.createOrDeleteBookmark(member.getId(), 0L))
                    .isInstanceOf(JobPostingException.class)
                    .hasMessage(JobPostingErrorCode.JOB_POSTING_NOT_FOUND.getMessage());
        }
    }
}
