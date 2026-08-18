package org.sopt.kareer.domain.jobposting.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.entity.JobPostingBookmark;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.jobposting.util.ResumeContextService;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.enums.RagType;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.RagSearchService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingRecommendFacade {

    private final ResumeContextService resumeContextService;
    private final OpenAiService openAiService;
    private final RagSearchService ragSearchService;
    private final MemberContextBuilder memberContextBuilder;
    private final ActionItemRepository actionItemRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;

    public JobPostingListResponse recommend(Long memberId, List<MultipartFile> files, boolean includeCompletedTodos) {

        if (files != null && files.size() > 2) {
            throw new JobPostingException(JobPostingErrorCode.TOO_MANY_FILES);
        }

        var memberContext = memberContextBuilder.load(memberId);
        String userContext = memberContext.contextText();

        String enrichedUserContext = userContext;
        if (includeCompletedTodos) {
            List<ActionItem> completedTodos = actionItemRepository.findAllByMemberIdAndCompletedTrue(memberId);
            String userTodoText = completedTodos.stream()
                    .map(todo -> "- " + todo.getTitle())
                    .collect(Collectors.joining("\n"));
            enrichedUserContext = """
                    %s

                    [USER_COMPLETED_TODO]
                   %s""".formatted(userContext, userTodoText);
        }

        String resumeContext = resumeContextService.buildContext(files);

        String combinedContext = """
                [USER_CONTEXT]
                %s

                [RESUME_OR_COVER_LETTER]
                %s

                """.formatted(enrichedUserContext, resumeContext);

        List<Document> retrieved = ragSearchService.search(combinedContext, 4, RagType.JOBPOSTING);
        List<Long> recommendedIds = openAiService.recommendJobPosting(combinedContext, retrieved);

        List<JobPosting> jobPostings = jobPostingRepository.findAllById(recommendedIds);
        Map<Long, JobPosting> jobPostingMap = jobPostings.stream()
                .collect(Collectors.toMap(JobPosting::getId, Function.identity()));
        List<JobPosting> orderedJobPostings = recommendedIds.stream()
                .map(jobPostingMap::get)
                .filter(Objects::nonNull)
                .toList();

        List<JobPostingBookmark> bookmarked = jobPostingBookmarkRepository
                .findAllByMemberIdAndJobPostingId(memberId, recommendedIds);
        Set<Long> bookmarkedIds = bookmarked.stream()
                .map(b -> b.getJobPosting().getId())
                .collect(Collectors.toSet());

        List<JobPostingResponse> responses = orderedJobPostings.stream()
                .map(jp -> JobPostingResponse.from(jp, bookmarkedIds.contains(jp.getId())))
                .toList();

        return new JobPostingListResponse(responses);
    }
}
