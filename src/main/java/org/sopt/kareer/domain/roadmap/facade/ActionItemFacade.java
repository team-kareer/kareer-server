package org.sopt.kareer.domain.roadmap.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemResponse;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.service.ActionItemCommandService;
import org.sopt.kareer.domain.roadmap.service.ActionItemQueryService;
import org.sopt.kareer.domain.roadmap.service.dto.response.ActionItemDetail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionItemFacade {

    private final ActionItemQueryService actionItemQueryService;
    private final ActionItemCommandService actionItemCommandService;

    @Transactional
    public void toggleCompletion(Long memberId, Long actionItemId) {
        actionItemCommandService.toggleCompletion(memberId, actionItemId);
    }

    public ActionItemListResponse getAllActionItems(Long memberId) {
        List<ActionItemDetail> items = actionItemQueryService.getAllActiveItems(memberId);

        List<ActionItemResponse> visaItems = items.stream()
                .filter(d -> d.type() == ActionItemType.VISA)
                .map(d -> new ActionItemResponse(d.id(), d.title(), d.deadline(), d.completed()))
                .toList();

        List<ActionItemResponse> careerItems = items.stream()
                .filter(d -> d.type() == ActionItemType.CAREER)
                .map(d -> new ActionItemResponse(d.id(), d.title(), d.deadline(), d.completed()))
                .toList();

        return new ActionItemListResponse(visaItems, careerItems);
    }
}
