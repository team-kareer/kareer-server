package org.sopt.kareer.domain.roadmap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActionItemCreateRequest(
        @NotBlank(message = "액션 아이템 타입은 필수 입력값입니다.")
        @Schema(description = "액션 아이템 타입", example = "CAREER")
        String type,

        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
        @Schema(description = "액션 아이템 제목", example = "이력서 작성하기")
        String title,

        @NotNull(message = "마감일은 필수 입력값입니다.")
        @Future(message = "마감일은 오늘보다 이후 날짜여야 합니다.")
        @Schema(description = "액션 아이템 마감일", example = "2026-09-01")
        LocalDate deadline
) {
}
