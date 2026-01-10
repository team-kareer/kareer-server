package org.sopt.kareer.domain.jobposting.fixture;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.time.LocalDate;

public class JobPostingFixture {

    public static final String POST_TITLE = "test";
    public static final String COMPANY = "test";
    public static final String WEBSITE_URL = "test";
    public static final String IMAGE_URL = "test";
    public static final String PREFERRED_VISA = "test";
    public static final String PREFERRED_LANGUAGE = "test";
    public static final String ARRANGEMENT = "test";
    public static final String ADDRESS = "test";

    public static JobPosting getJobPosting(Long recruitId, LocalDate deadline){
        return JobPosting.builder()
                .postTitle(POST_TITLE)
                .recruitId(recruitId)
                .deadline(deadline)
                .company(COMPANY)
                .websiteUrl(WEBSITE_URL)
                .imageUrl(IMAGE_URL)
                .preferredVisa(PREFERRED_VISA)
                .preferredLanguage(PREFERRED_LANGUAGE)
                .arrangement(ARRANGEMENT)
                .address(ADDRESS)
                .build();
    }

}
