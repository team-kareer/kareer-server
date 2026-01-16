package org.sopt.kareer.domain.roadmap.dto.response;

import java.util.List;

public record ActionItemListResponse(
        List<ActionItemResponse> visaActionItems,
        List<ActionItemResponse> careerActionItems
) {
}
