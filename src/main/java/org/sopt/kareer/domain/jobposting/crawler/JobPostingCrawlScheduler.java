package org.sopt.kareer.domain.jobposting.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobPostingCrawlScheduler {

    private final JobPostingCrawler jobPostingCrawler;

    @Scheduled(cron = "0 0 3 * * *")
    public void crawlJobPostingScheduled() {
        try {
            JobPostingCrawlListResponse response = jobPostingCrawler.crawlAllJobPostings();
            log.info("Scheduled crawl done. size={}", response.crawlResponses().size());
        } catch (Exception e) {
            log.error("Scheduled crawl failed", e);
        }
    }
}
