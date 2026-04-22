package org.sopt.kareer.domain.jobposting.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JobPostingQueryServiceTest {

    @Autowired
    private JobPostingQueryService jobPostingQueryService;

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

    @DisplayName("북마크한 채용공고 목록을 조회한다.")
    @Test
    void getBookmarkedJobPostings_success() {
        //given
        Member member = memberRepository.save(MemberFixture.getMember());
        JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now().plusDays(1)));
        jobPostingCommandService.createOrDeleteBookmark(member, jobPosting1.getId());
        jobPostingCommandService.createOrDeleteBookmark(member, jobPosting2.getId());

        //when
        List<JobPosting> result = jobPostingQueryService.getBookmarkedJobPostings(member.getId());

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(JobPosting::getId)
                .containsExactlyInAnyOrder(jobPosting1.getId(), jobPosting2.getId());
    }

    @DisplayName("북마크한 채용공고가 없으면 빈 리스트를 반환한다.")
    @Test
    void getBookmarkedJobPostings_empty() {
        //given
        Member member = memberRepository.save(MemberFixture.getMember());

        //when
        List<JobPosting> result = jobPostingQueryService.getBookmarkedJobPostings(member.getId());

        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("다른 회원의 북마크는 조회되지 않는다.")
    @Test
    void getBookmarkedJobPostings_onlyOwnBookmarks() {
        //given
        Member member1 = memberRepository.save(MemberFixture.getMember("provider-1"));
        Member member2 = memberRepository.save(MemberFixture.getMember("provider-2"));
        JobPosting jobPosting1 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        JobPosting jobPosting2 = jobPostingRepository.save(JobPostingFixture.getJobPosting(LocalDate.now()));
        jobPostingCommandService.createOrDeleteBookmark(member1, jobPosting1.getId());
        jobPostingCommandService.createOrDeleteBookmark(member2, jobPosting2.getId());

        //when
        List<JobPosting> result = jobPostingQueryService.getBookmarkedJobPostings(member1.getId());

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(jobPosting1.getId());
    }
}
