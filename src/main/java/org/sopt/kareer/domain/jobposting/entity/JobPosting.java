package org.sopt.kareer.domain.jobposting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String company;

    @Column(nullable = false)
    private LocalDate deadline;

    private String websiteUrl;

    private String imageUrl;

    private String preferredVisa;

    private String preferredLanguage;

    private String arrangement;

    private String address;

}
