package org.sopt.kareer.domain.roadmap.dto.response;

import java.time.LocalDate;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;

public record ActionItemResponse(
        Long actionItemId,
        String title,
        LocalDate deadline,
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
