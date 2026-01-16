package org.sopt.kareer.domain.jobposting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingRecommendResponse;
import org.sopt.kareer.domain.jobposting.service.JobPostingCrawler;
import org.sopt.kareer.domain.jobposting.service.JobPostingService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.RECOMMEND_JOBPOSTING;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-postings")
public class JobPostingController {

    private final JobPostingCrawler jobPostingCrawler;
    private final JobPostingService jobPostingService;

    @Tag(name = "채용 공고 관련 API")
    @Operation(summary = "채용 공고 크롤링 (Server Only)")
    @GetMapping("crawl")
    public ResponseEntity<BaseResponse<JobPostingCrawlListResponse>> crawlJobPostings(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingCrawler.crawlJobPostingForTest(limit), "채용 공고 크롤링에 성공하였습니다."));
    }

    @Tag(name = "채용 공고 관련 API")
    @Operation(summary = "채용 공고 추천", description = "사용자가 업로드한 이력서/자소서, 사용자 정보 기반으로 채용 공고를 추천합니다.")
    @CustomExceptionDescription(RECOMMEND_JOBPOSTING)
    @PostMapping(value = "recommend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<JobPostingRecommendResponse>> recommendJobPostings(
            @AuthenticationPrincipal Long memberId,
            @Parameter List<MultipartFile> files
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingService.recommend(memberId, files), "채용 공고 추천에 성공하였습니다."));
    }

}
