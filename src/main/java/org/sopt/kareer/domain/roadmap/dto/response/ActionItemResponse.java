package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;

@Schema(description = "Action Item 응답(Todo)")
public record ActionItemResponse(
        @Schema(description = "Action Item id", example = "1")
        Long actionItemId,
        @Schema(description = "Action Item 제목", example = "Prepare Resume")
        String title,
        @Schema(description = "Action Item 마감일", example = "2025-10-15")
        LocalDate deadline,
        @Schema(description = "Action Item 완료 여부", example = "false")
        boolean completed
) {

    public static ActionItemResponse from(ActionItem actionItem) {
        return new ActionItemResponse(
                actionItem.getId(),
                actionItem.getTitle(),
                actionItem.getDeadline(),
                actionItem.getCompleted()
        );
    }
}
