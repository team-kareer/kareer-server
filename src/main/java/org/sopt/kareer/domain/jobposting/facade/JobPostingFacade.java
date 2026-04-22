package org.sopt.kareer.domain.jobposting.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.service.JobPostingCommandService;
import org.sopt.kareer.domain.jobposting.service.JobPostingQueryService;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingFacade {

    private final JobPostingRecommendFacade jobPostingRecommendFacade;
    private final JobPostingQueryService jobPostingQueryService;
    private final JobPostingCommandService jobPostingCommandService;
    private final MemberQueryService memberQueryService;

    public JobPostingListResponse recommend(Long memberId, List<MultipartFile> files, boolean includeCompletedTodos) {
        return jobPostingRecommendFacade.recommend(memberId, files, includeCompletedTodos);
    }

    @Transactional
    public void createOrDeleteBookmark(Long memberId, Long jobPostingId) {
        Member member = memberQueryService.getMemberById(memberId);
        jobPostingCommandService.createOrDeleteBookmark(member, jobPostingId);
    }

    public JobPostingListResponse getJobPostingBookmarks(Long memberId) {
        memberQueryService.getMemberById(memberId);
        return jobPostingQueryService.getJobPostingBookmarks(memberId);
    }
}
