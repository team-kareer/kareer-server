package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemResponse;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;

    @Transactional
    public void toggleCompletion(Long memberId, Long actionItemId) {
        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.ACTION_ITEM_NOT_FOUND));

        if (!actionItem.getMember().getId().equals(memberId)) {
            throw new RoadMapException(RoadmapErrorCode.ACTION_ITEM_FORBIDDEN);
        }

        if (actionItem.getStatus() == ActionItemStatus.INACTIVE) {
            throw new RoadMapException(RoadmapErrorCode.ACTION_ITEM_INACTIVE);
        }

        PhaseAction phaseAction = actionItem.getPhaseAction();

        if (phaseAction.getCompleted()) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_ACTION_ALREADY_COMPLETED);
        }

        actionItem.toggleCompletion();

        if (actionItem.getCompleted()) {
            boolean hasIncompleteItems = actionItemRepository.existsByPhaseActionIdAndCompletedFalse(
                    phaseAction.getId());
            if (!hasIncompleteItems) {
                phaseAction.markCompleted();
                List<ActionItem> phaseActionItems = actionItemRepository.findAllByPhaseActionId(phaseAction.getId());
                phaseActionItems.forEach(ActionItem::deactivate);
            }
        }
    }

    public ActionItemListResponse getAllActionItems(Long memberId) {
        List<ActionItem> activeActionItems = actionItemRepository
                .findAllByMemberIdAndStatus(memberId, ActionItemStatus.ACTIVE);

        List<ActionItemResponse> visaActionItems = activeActionItems.stream()
                .filter(actionItem -> actionItem.getActionsType() == ActionItemType.VISA)
                .map(ActionItemResponse::from)
                .toList();

        List<ActionItemResponse> careerActionItems = activeActionItems.stream()
                .filter(actionItem -> actionItem.getActionsType() == ActionItemType.CAREER)
                .map(ActionItemResponse::from)
                .toList();

        return new ActionItemListResponse(visaActionItems, careerActionItems);
    }
}
