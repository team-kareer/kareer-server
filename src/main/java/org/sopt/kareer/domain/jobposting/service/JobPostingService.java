package org.sopt.kareer.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingRecommendResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.jobposting.util.ResumeContextService;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.member.util.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JobPostingService {

    private final ResumeContextService resumeContextService;
    private final RagService ragService;
    private final OpenAiService openAiService;
    private final JobPostingRepository jobPostingRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;
    private final MemberContextBuilder memberContextBuilder;
    private final MemberService memberService;

    public JobPostingRecommendResponse recommend(Long memberId, List<MultipartFile> files) {

        if (files != null && files.size() > 2) {
            throw new JobPostingException(JobPostingErrorCode.TOO_MANY_FILES);
        }

        var memberContext = memberContextBuilder.load(memberId);
        String userContext = memberContext.contextText();

        String resumeContext = resumeContextService.buildContext(files);

        String combinedContext = """
                [USER_CONTEXT]
                %s

                [RESUME_OR_COVER_LETTER]
                %s
                """.formatted(userContext, resumeContext);

        List<Document> retrieved = ragService.search(combinedContext, 4, RagType.JOBPOSTING);

        List<Long> recommendedIds = openAiService.recommendJobPosting(userContext, retrieved);

        List<JobPostingResponse> responses = jobPostingRepository.findAllById(recommendedIds).stream()
                .map(JobPostingResponse::from)
                .toList();

        Map<Long, JobPostingResponse> map = responses.stream()
                .collect(Collectors.toMap(JobPostingResponse::jobPostingId, r -> r));
        List<JobPostingResponse> ordered = recommendedIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();

        return new JobPostingRecommendResponse(ordered);
    }

    @Transactional
    public void createBookmark(Long memberId, Long jobPostingId) {

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new JobPostingException(JobPostingErrorCode.JOB_POSTING_NOT_FOUND));

        Member member = memberService.getById(memberId);

        if(jobPostingBookmarkRepository.existsByJobPostingIdAndMemberId(jobPostingId, memberId)){
            jobPostingBookmarkRepository.deleteByJobPostingIdAndMemberId(jobPostingId, memberId);
            return;
        }

        JobPostingBookmark jobPostingBookmark = JobPostingBookmark.create(member, jobPosting);
        jobPostingBookmarkRepository.save(jobPostingBookmark);

    }

}
