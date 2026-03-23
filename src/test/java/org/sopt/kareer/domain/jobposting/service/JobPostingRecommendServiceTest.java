package org.sopt.kareer.domain.jobposting.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.jobposting.util.ResumeContextService;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.RagEmbeddingService;
import org.sopt.kareer.global.external.ai.service.RagSearchService;
import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class JobPostingRecommendServiceTest {

    @InjectMocks
    private JobPostingService jobPostingService;

    @Mock
    private ResumeContextService resumeContextService;

    @Mock
    private RagEmbeddingService ragEmbeddingService;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobPostingBookmarkRepository jobPostingBookmarkRepository;

    @Mock
    private MemberContextBuilder memberContextBuilder;

    @Mock
    private ActionItemRepository actionItemRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private RagSearchService ragSearchService;

    @DisplayName("채용 공고 추천 시 결과는 마감일을 기준으로 오름차순으로 정렬되어 반환된다.")
    @Test
    void recommend(){
       //given
        Member member = mock(Member.class);
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        String contextText = "MEMBER_CONTEXT";
        MemberContextBuilder.MemberAndContext memberAndContext = new MemberContextBuilder.MemberAndContext(member, contextText);
        when(memberContextBuilder.load(member.getId())).thenReturn(memberAndContext);

        when(resumeContextService.buildContext(anyList())).thenReturn("RESUME_CONTEXT");

        List<Document> retrieved = List.of(
                new Document("doc1"),
                new Document("doc2"),
                new Document("doc3"),
                new Document("doc4")
        );
        when(ragSearchService.search(anyString(),eq(4), eq(RagType.JOBPOSTING))).thenReturn(retrieved);

        List<Long> recommendedIds = List.of(3L, 1L, 2L, 4L);
        when(openAiService.recommendJobPosting(anyString(), eq(retrieved))).thenReturn(recommendedIds);

        JobPosting jobPosting1 = getJobPosting(1L, LocalDate.of(2026,2,2));
        JobPosting jobPosting2 = getJobPosting(2L, LocalDate.of(2026,2,3));
        JobPosting jobPosting3 = getJobPosting(3L, LocalDate.of(2026,2,1));
        JobPosting jobPosting4 = getJobPosting(4L, LocalDate.of(2026,2,4));
        when(jobPostingRepository.findAllById(recommendedIds)).thenReturn(List.of(jobPosting1, jobPosting3, jobPosting2, jobPosting4));

        JobPostingBookmark jobPostingBookmark = JobPostingBookmark.builder()
                .jobPosting(jobPosting1)
                .member(member)
                .build();
        when(jobPostingBookmarkRepository.findAllByMemberIdAndJobPostingId(member.getId(),recommendedIds))
                .thenReturn(List.of(jobPostingBookmark));

        //when
        JobPostingListResponse response = jobPostingService.recommend(member.getId(), files, false);

        //then
        assertThat(response.jobPostingResponses()).extracting("jobPostingId")
                .containsExactly(3L, 1L, 2L, 4L);
    }

    @DisplayName("이력서 또는 자소서 PDF 파일이 3개 이상이면 예외가 발생한다.")
    @Test
    void recommendWithThreeFiles(){
       //given
       Long memberId = 1L;
       List<MultipartFile> files = List.of(mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class));

        //when && then
        assertThatThrownBy(() -> jobPostingService.recommend(memberId,files,false))
                .hasMessage(JobPostingErrorCode.TOO_MANY_FILES.getMessage());
    }

    private JobPosting getJobPosting(Long id, LocalDate deadline) {
        JobPosting jp = mock(JobPosting.class);
        when(jp.getId()).thenReturn(id);
        when(jp.getDeadline()).thenReturn(deadline);
        return jp;
    }
}
