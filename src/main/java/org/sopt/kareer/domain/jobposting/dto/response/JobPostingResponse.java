package org.sopt.kareer.domain.jobposting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;

import java.time.LocalDate;
import java.util.List;

@Builder
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
        List<String> arrangement,

        @Schema(description = "주소")
        List<String> address,

        @Schema(description = "공고 url")
        String websiteUrl,

        @Schema(description = "북마크 여부")
        boolean isBookmarked
) {
        public static JobPostingResponse from(JobPosting jobPosting, boolean isbookmarked) {
                return JobPostingResponse.builder()
                        .jobPostingId(jobPosting.getId())
                        .deadline(jobPosting.getDeadline())
                        .imageUrl(jobPosting.getImageUrl())
                        .company(jobPosting.getCompany())
                        .title(jobPosting.getPostTitle())
                        .arrangement(splitText(jobPosting.getArrangement()))
                        .address(splitText(jobPosting.getAddress()))
                        .websiteUrl(jobPosting.getWebsiteUrl())
                        .isBookmarked(isbookmarked)
                        .build();
        }

        private static List<String> splitText(String text) {
                if (text == null || text.isBlank()) {
                        return List.of();
                }

                return List.of(text.split(","))
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
        }
}
