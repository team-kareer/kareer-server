package org.sopt.kareer.domain.jobposting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.time.LocalDate;

public record JobPostingResponse(

        @Schema(description = "채용 공고 ID", example = "1")
        Long jobPostingId,

        @Schema(description = "마감일", example = "2026/01/01")
        LocalDate deadline,

        @Schema(description = "이미지 url")
        String imageUrl,

        @Schema(description = "회사명", example = "THEIA")
        String company,

        @Schema(description = "채용 공고 제목", example = "Hiring Japanese Marketers")
        String title,

        @Schema(description = "근무 형태", example = "Part-time Worker")
        String arrangement,

        @Schema(description = "주소", example = "Seocho-gu, Seoul")
        String address,

        @Schema(description = "공고 url")
        String websiteUrl
) {
        public static JobPostingResponse from(JobPosting jobPosting) {
                return new JobPostingResponse(
                        jobPosting.getId(),
                        jobPosting.getDeadline(),
                        jobPosting.getImageUrl(),
                        jobPosting.getCompany(),
                        jobPosting.getPostTitle(),
                        jobPosting.getArrangement(),
                        jobPosting.getAddress(),
                        jobPosting.getWebsiteUrl()
                );
        }
}
