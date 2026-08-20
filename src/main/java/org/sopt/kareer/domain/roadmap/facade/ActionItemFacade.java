package org.sopt.kareer.domain.roadmap.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.roadmap.dto.request.ActionItemCreateRequest;
import org.sopt.kareer.domain.roadmap.dto.request.ActionItemUpdateRequest;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemResponse;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.service.actionitem.ActionItemCommandService;
import org.sopt.kareer.domain.roadmap.service.actionitem.ActionItemQueryService;
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
    private final MemberQueryService memberQueryService;

    @Transactional
    public ActionItemResponse createActionItem(Long memberId, ActionItemCreateRequest request) {
        Member member = memberQueryService.getMemberById(memberId);
        ActionItem created = actionItemCommandService.createActionItem(
                member,
                ActionItemType.from(request.type()),
                request.title(),
                request.deadline()
        );
        return ActionItemResponse.from(created);
    }

    @Transactional
    public ActionItemResponse updateActionItem(
            Long memberId,
            Long actionItemId,
            ActionItemUpdateRequest request
    ) {
        ActionItem updated = actionItemCommandService.updateActionItem(
                memberId,
                actionItemId,
                request.title(),
                request.deadline()
        );
        return ActionItemResponse.from(updated);
    }

    @Transactional
    public void deleteActionItem(Long memberId, Long actionItemId) {
        actionItemCommandService.deleteActionItem(memberId, actionItemId);
    }

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
