package org.sopt.kareer.domain.jobposting.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.crawler.JobPostingCrawler;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("jobpostings")
public class JobPostingController {

    private final JobPostingCrawler jobPostingCrawler;

    @GetMapping("crawl")
    public ResponseEntity<BaseResponse<JobPostingCrawlListResponse>> crawlJobPostings(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(jobPostingCrawler.crawlJobPostingForTest(limit), "채용 공고 크롤링에 성공하였습니다."));
    }

}
