package org.sopt.kareer.domain.jobposting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.*;

@Tag(name = "채용 공고 관련 API")
public interface JobPostingApi {

    @GetMapping("crawl")
    @Operation(summary = "채용 공고 크롤링 (Server Only)")
    ResponseEntity<BaseResponse<JobPostingCrawlListResponse>> crawlJobPostings(@RequestParam(defaultValue = "5") int limit);

    @PostMapping(value = "recommend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "채용 공고 추천", description = "사용자가 업로드한 이력서/자소서, 사용자 정보 기반으로 채용 공고를 추천합니다.")
    @CustomExceptionDescription(RECOMMEND_JOBPOSTING)
    ResponseEntity<BaseResponse<JobPostingListResponse>> recommendJobPostings(
            @AuthenticationPrincipal Long memberId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "includeCompletedTodo", defaultValue = "false") boolean includeCompletedTodos);

    @PostMapping("{jobPostingId}/bookmarks")
    @Operation(summary = "채용 공고 북마크 추가/삭제", description = "사용자가 추천된 채용 공고를 추가하거나 삭제합니다.")
    @CustomExceptionDescription(CREATE_BOOKMARK)
    ResponseEntity<BaseResponse<Void>> createJobPostingBookmark(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long jobPostingId);

    @GetMapping("bookmarks")
    @Operation(summary = "채용 공고 북마크 조회", description = "사용자가 북마크한 채용 공고를 조회합니다.")
    @CustomExceptionDescription(GET_BOOKMARK)
    ResponseEntity<BaseResponse<JobPostingListResponse>> getJobPostingBookmarks(@AuthenticationPrincipal Long memberId);
}
