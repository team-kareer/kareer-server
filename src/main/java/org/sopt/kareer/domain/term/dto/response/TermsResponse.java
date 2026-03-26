package org.sopt.kareer.domain.term.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.term.entity.Term;

import java.util.List;

@Schema(description = "약관 응답")
public record TermsResponse(
        @Schema(description = "약관 리스트")
        List<TermResponse> terms
) {
    public static TermsResponse from(List<TermResponse> terms) {
        return new TermsResponse(terms);
    }

    public record TermResponse(
            @Schema(description = "약관 고유번호", example="1")
            Long termId,

            @Schema(description = "약관 제목", example="Terms of Service")
            String title,

            @Schema(description = "약관 내용", example="1. Purpose ~")
            String content
    ) {
        public static TermResponse from(Term term) {
            return new TermResponse(
                    term.getId(),
                    term.getTitle(),
                    term.getContent()
            );
        }
    }
}
