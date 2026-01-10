package org.sopt.kareer.domain.jobposting.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingCrawlResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.domain.jobposting.util.CrawledTextUtil;
import org.sopt.kareer.global.config.WebDriverFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.sopt.kareer.domain.jobposting.constant.JobPostingCrawlConstants.*;
import static org.sopt.kareer.domain.jobposting.util.CrawledTextUtil.parseRecruitId;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingCrawler {

    private final WebDriverFactory webDriverFactory;

    private final JobPostingRepository jobPostingRepository;

    // 서버 테스트용
    public JobPostingCrawlListResponse crawlJobPostingForTest(int limit) {
        if (limit <= 0) return new JobPostingCrawlListResponse(List.of());
        return crawlJobPostingList(limit);
    }

    // 스케줄러 호출용
    public JobPostingCrawlListResponse crawlAllJobPostings() {
        return crawlJobPostingList(null);
    }

    public JobPostingCrawlListResponse crawlJobPostingList(Integer limit) {

        WebDriver driver = webDriverFactory.create();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            driver.get(BASE_URL);
            waitDocReady(driver, wait);

            Set<String> recruitUrls = (limit == null)
                    ? collectAllRecruitUrls(driver, wait)
                    : collectRecruitUrls(driver, wait, limit);

            List<String> targets = (limit == null)
                    ? recruitUrls.stream().toList()
                    : recruitUrls.stream().limit(limit).toList();

            List<JobPostingCrawlResponse> out = new ArrayList<>();

            for(int i = 0; i < targets.size(); i++) {
                String url = targets.get(i);
                Long recruitId = parseRecruitId(url);

                if (recruitId == null) {
                    log.warn("Skip - recruitId parse failed. url={}", url);
                    continue;
                }

                if(jobPostingRepository.existsByRecruitId(recruitId)) {
                    log.info("Skip - already exists. idx={}/{} recruitId={}", i, targets.size(), recruitId);
                    continue;
                }

                log.info("Crawling detail start. idx={}/{} recruitId={} url={}",
                        i, targets.size(), recruitId, url);

                try {
                    JobPostingCrawlResponse response = crawlJobPosting(driver, wait, url);
                    out.add(response);

                } catch (Exception e) {
                    log.warn("Crawling detail failed. recruitId={} url={}", recruitId, url, e);
                } finally {
                    sleepRandom(250, 600);
                }


            }

            return new JobPostingCrawlListResponse(out);

        } finally {
            try { driver.quit(); } catch (Exception ignored) {}
        }
    }

    private JobPostingCrawlResponse crawlJobPosting(WebDriver driver, WebDriverWait wait, String url) {
        driver.get(url);
        waitDocReady(driver, wait);

        String postTitle = textOrNull(findFirst(driver, By.cssSelector("h1")));
        if (postTitle == null) postTitle = textOrNull(findFirst(driver, By.cssSelector("h2")));

        String pageText = driver.findElement(By.tagName("body")).getText();

        Long recruitId = parseRecruitId(url);

        LocalDate deadline = CrawledTextUtil.extractDeadline(pageText);

        Map<String, String> jobSummaryFields = CrawledTextUtil.extractJobSummaryFields(pageText);

        String arrangement = jobSummaryFields.get(LABEL_ARRANGEMENT);

        String preferredVisa = jobSummaryFields.get(LABEL_VISA);
        String preferredLanguage = jobSummaryFields.get(LABEL_LANGUAGE);

        String siteAddress = jobSummaryFields.get(LABEL_SITE);

        Map<String, String> sections = CrawledTextUtil.extractSections(pageText, SECTION_HEADERS);

        String companySectionText = sections.get(LABEL_COMPANY);

        String company = CrawledTextUtil.extractCompanyName(companySectionText);

        String imageUrl = extractImageUrl(driver);

        JobPosting jobPosting = JobPosting.create(postTitle, recruitId, company, deadline, url, imageUrl, preferredVisa,
                preferredLanguage, arrangement, siteAddress);

        jobPostingRepository.save(jobPosting);

        return JobPostingCrawlResponse.from(jobPosting);
    }


    private Set<String> collectRecruitUrlsInternal(
            WebDriver driver,
            WebDriverWait wait,
            Integer limit,
            int maxPages,
            boolean stopOnEmptyStreak
    ) {
        Set<String> urls = new LinkedHashSet<>();
        int emptyNewPageStreak = 0;

        for (int page = 1; page <= maxPages; page++) {

            if (limit != null && urls.size() >= limit) break;

            String pageUrl = BASE_URL
                             + (BASE_URL.contains("?") ? "&" : "?")
                             + "page=" + page;

            driver.get(pageUrl);
            waitDocReady(driver, wait);

            @SuppressWarnings("unchecked")
            List<String> hrefs =
                    (List<String>) ((JavascriptExecutor) driver)
                            .executeScript(JS_COLLECT_ALL_HREFS);

            if (hrefs == null || hrefs.isEmpty()) break;

            int before = urls.size();

            for (String href : hrefs) {
                if (href == null) continue;

                if (RECRUIT_DETAIL_URL_PATTERN.matcher(href).matches()) {
                    urls.add(href.split("#")[0]);

                    if (limit != null && urls.size() >= limit) break;
                }
            }

            int added = urls.size() - before;

            if (stopOnEmptyStreak) {
                if (added == 0) {
                    emptyNewPageStreak++;
                    if (emptyNewPageStreak >= 2) break;
                } else {
                    emptyNewPageStreak = 0;
                }
            }

            sleepRandom(150, 350);
        }

        return urls;
    }

    private Set<String> collectRecruitUrls(WebDriver driver, WebDriverWait wait, int limit) {
        return collectRecruitUrlsInternal(
                driver,
                wait,
                limit,
                50,
                false
        );
    }

    private Set<String> collectAllRecruitUrls(WebDriver driver, WebDriverWait wait) {
        return collectRecruitUrlsInternal(
                driver,
                wait,
                null,
                MAX_LIST_PAGES,
                true
        );
    }

    private String extractImageUrl(WebDriver driver) {

        List<WebElement> sliderLinks = driver.findElements(
                By.cssSelector(SLIDER_IMAGE_CSS)
        );

        if (sliderLinks.isEmpty()) {
            return null;
        }

        String href = sliderLinks.get(0).getAttribute("href");
        return isBlank(href) ? null : href;
    }

    private void waitDocReady(WebDriver driver, WebDriverWait wait) {
        wait.until(d -> ((JavascriptExecutor) d).executeScript(JS_DOCUMENT_READY_STATE).equals("complete"));
    }

    private WebElement findFirst(WebDriver driver, By by) {
        try {
            return driver.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private String textOrNull(WebElement el) {
        if (el == null) return null;
        String t = el.getText();
        return (t == null || t.isBlank()) ? null : t.trim();
    }

    private void sleepRandom(int minMs, int maxMs) {
        try {
            Thread.sleep((long) (minMs + Math.random() * (maxMs - minMs)));
        } catch (InterruptedException ignored) {}
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
