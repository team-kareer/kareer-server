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
    private String postTitle;

    @Column(nullable = false, unique = true)
    private Long recruitId;

    private String company;

    private LocalDate deadline;

    private String websiteUrl;

    private String imageUrl;

    private String preferredVisa;

    private String preferredLanguage;

    private String arrangement;

    private String address;

    @Builder
    private JobPosting(String postTitle, Long recruitId, String company, LocalDate deadline, String websiteUrl, String imageUrl,
                      String preferredVisa, String preferredLanguage, String arrangement, String address) {
        this.postTitle = postTitle;
        this.recruitId = recruitId;
        this.company = company;
        this.deadline = deadline;
        this.websiteUrl = websiteUrl;
        this.imageUrl = imageUrl;
        this.preferredVisa = preferredVisa;
        this.preferredLanguage = preferredLanguage;
        this.arrangement = arrangement;
        this.address = address;
    }

    public static JobPosting create(String postTitle, Long recruitId, String company, LocalDate deadline,
                                    String websiteUrl, String imageUrl, String preferredVisa, String preferredLanguage,
                                    String arrangement, String address) {
        return JobPosting.builder()
                .postTitle(postTitle)
                .recruitId(recruitId)
                .company(company)
                .deadline(deadline)
                .websiteUrl(websiteUrl)
                .imageUrl(imageUrl)
                .preferredVisa(preferredVisa)
                .preferredLanguage(preferredLanguage)
                .arrangement(arrangement)
                .address(address)
                .build();
    }


}
