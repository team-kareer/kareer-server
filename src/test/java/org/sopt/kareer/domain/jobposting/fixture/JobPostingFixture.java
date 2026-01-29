package org.sopt.kareer.domain.jobposting.fixture;

import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.time.LocalDate;

public class JobPostingFixture {

    public static final String POST_TITLE = "test-title";
    public static final String COMPANY = "test-company";
    public static final String WEBSITE_URL = "test-website-url";
    public static final String IMAGE_URL = "test-image-url";
    public static final String PREFERRED_VISA = "test-preferred-visa";
    public static final String PREFERRED_LANGUAGE = "test-preferred-language";
    public static final String ARRANGEMENT = "test-arrangement";
    public static final String ADDRESS = "test-address";
    public static final String DETAIL = "test-detail";
    public static final String CAREER = "test-career";
    public static final String EDUCATION = "test-education";

    public static JobPosting getJobPosting(LocalDate deadline){
        return JobPosting.builder()
                .postTitle(POST_TITLE)
                .deadline(deadline)
                .company(COMPANY)
                .websiteUrl(WEBSITE_URL)
                .imageUrl(IMAGE_URL)
                .preferredVisa(PREFERRED_VISA)
                .preferredLanguage(PREFERRED_LANGUAGE)
                .arrangement(ARRANGEMENT)
                .address(ADDRESS)
                .detail(DETAIL)
                .career(CAREER)
                .education(EDUCATION)
                .build();
    }

}
