package org.sopt.kareer.domain.jobposting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.time.LocalDate;

@Builder
public record JobPostingCrawlResponse(

        @Schema(description = "채용 공고 제목", example = "D2, D10 Golf Caddy Training and Employment Linkage Program for International")
        String postTitle,

        @Schema(description = "회사명", example = "팀잡플로이")
        String company,

        @Schema(description = "채용 공고 마감일", example = "2026-01-01")
        LocalDate deadline,

        @Schema(description = "채용 공고 사이트 링크")
        String websiteUrl,

        @Schema(description = "공고 이미지 url")
        String imageUrl,

        @Schema(description = "비자 요구사항", example = "D-2, D-10")
        String preferredVisa,

        @Schema(description = "언어 요구사항", example = "you must prepare for TOPIK Level 4 to obtain F2R.")
        String preferredLanguage,

        @Schema(description = "근무 형태", example = "Intern, Regular work")
        String arrangement,

        @Schema(description = "주소", example = "Chungnam Asan-si, Gangwon Gangneung-si")
        String address
) {

    public static JobPostingCrawlResponse from(JobPosting jobPosting) {
        return JobPostingCrawlResponse.builder()
                .postTitle(jobPosting.getPostTitle())
                .company(jobPosting.getCompany())
                .deadline(jobPosting.getDeadline())
                .websiteUrl(jobPosting.getWebsiteUrl())
                .imageUrl(jobPosting.getImageUrl())
                .preferredVisa(jobPosting.getPreferredVisa())
                .preferredLanguage(jobPosting.getPreferredLanguage())
                .arrangement(jobPosting.getArrangement())
                .address(jobPosting.getAddress())
                .build();
    }
}
