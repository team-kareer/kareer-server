package org.sopt.kareer.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "약관 동의 요청")
public record MemberTermsRequest(
        @Schema(description = "약관 동의 리스트")
        @NotEmpty(message = "약관 동의 목록은 비어있을 수 없습니다.")
        List<@NotNull(message = "약관 동의 항목은 null일 수 없습니다.") @Valid TermAgreement> agreements
) {
    public record TermAgreement(
            @Schema(description = "약관 고유번호", example="1")
            @NotNull(message = "약관 ID는 필수입니다.")
            Long termId,

            @Schema(description = "약관 동의여부", example="true")
            @NotNull(message = "동의 여부는 필수입니다.")
            boolean agreed
    ) {}
}
