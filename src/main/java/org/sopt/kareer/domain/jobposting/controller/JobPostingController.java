package org.sopt.kareer.domain.jobposting.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.service.JobPostingCrawler;
import org.sopt.kareer.domain.jobposting.service.JobPostingService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-postings")
public class JobPostingController implements JobPostingApi {

    private final JobPostingCrawler jobPostingCrawler;
    private final JobPostingService jobPostingService;

    @Override
    public ResponseEntity<BaseResponse<JobPostingCrawlListResponse>> crawlJobPostings(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingCrawler.crawlJobPostingForTest(limit), "채용 공고 크롤링에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<JobPostingListResponse>> recommendJobPostings(
            @AuthenticationPrincipal Long memberId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "includeCompletedTodo", defaultValue = "false") boolean includeCompletedTodos) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingService.recommend(memberId, files, includeCompletedTodos), "채용 공고 추천에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> createJobPostingBookmark(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long jobPostingId) {
        jobPostingService.createOrDeleteBookmark(memberId, jobPostingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("채용 공고 북마크 추가 / 삭제에 성공했습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<JobPostingListResponse>> getJobPostingBookmarks(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingService.getJobPostingBookmarks(memberId), "북마크 채용 공고 조회에 성공하였습니다."));
    }
}
