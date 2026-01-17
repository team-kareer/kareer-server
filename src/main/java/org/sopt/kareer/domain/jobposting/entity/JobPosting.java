package org.sopt.kareer.domain.jobposting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "JobPostings")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String arrangement;

    @Column(nullable = false)
    private String company;

    private LocalDate deadline;

    private String imageUrl;

    @Column(nullable = false)
    private String postTitle;

    @Column(nullable = false)
    private String preferredLanguage;

    @Column(nullable = false)
    private String preferredVisa;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private String career;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String websiteUrl;

    @Builder
    private JobPosting(String address, String arrangement, String company, LocalDate deadline, String imageUrl, String postTitle,
                       String preferredLanguage, String preferredVisa, String detail, String career, String education,
                       String websiteUrl) {
        this.address = address;
        this.arrangement = arrangement;
        this.company = company;
        this.deadline = deadline;
        this.imageUrl = imageUrl;
        this.postTitle = postTitle;
        this.preferredLanguage = preferredLanguage;
        this.preferredVisa = preferredVisa;
        this.detail = detail;
        this.career = career;
        this.education = education;
        this.websiteUrl = websiteUrl;
    }

    public static JobPosting create(String address, String arrangement, String company, LocalDate deadline, String imageUrl, String postTitle,
                                    String preferredLanguage, String preferredVisa, String detail, String career, String education,
                                    String websiteUrl){
        return JobPosting.builder().
                address(address).
                arrangement(arrangement).
                company(company).
                deadline(deadline).
                imageUrl(imageUrl).
                postTitle(postTitle).
                preferredLanguage(preferredLanguage).
                preferredVisa(preferredVisa).
                detail(detail).
                career(career).
                education(education).
                websiteUrl(websiteUrl).
                build();
    }
}
