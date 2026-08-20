package org.sopt.kareer.domain.roadmap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActionItemUpdateRequest(
        @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
        @Schema(description = "수정할 액션 아이템 제목", example = "이력서 초안 작성하기")
        String title,

        @Future(message = "마감일은 오늘보다 이후 날짜여야 합니다.")
        @Schema(description = "수정할 액션 아이템 마감일", example = "2026-09-10")
        LocalDate deadline
) {

    @AssertTrue(message = "제목과 마감일 중 하나 이상 입력해야 합니다.")
    public boolean isUpdateFieldPresent() {
        return title != null || deadline != null;
    }

    @AssertTrue(message = "제목은 공백일 수 없습니다.")
    public boolean isTitleValid() {
        return title == null || !title.isBlank();
    }
}
