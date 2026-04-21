package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionItemCommandService {

    private final ActionItemRepository actionItemRepository;

    public void toggleCompletion(Long memberId, Long actionItemId) {
        ActionItem actionItem = actionItemRepository.findByIdAndMemberId(actionItemId, memberId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.ACTION_ITEM_NOT_FOUND));

        if (actionItem.getStatus() == ActionItemStatus.INACTIVE) {
            throw new RoadMapException(RoadmapErrorCode.ACTION_ITEM_INACTIVE);
        }

        PhaseAction phaseAction = actionItem.getPhaseAction();

        if (phaseAction.getCompleted()) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_ACTION_ALREADY_COMPLETED);
        }

        actionItem.toggleCompletion();

        if (actionItem.getCompleted()) {
            boolean hasIncompleteItems = actionItemRepository.existsByPhaseActionIdAndCompletedFalse(phaseAction.getId());
            if (!hasIncompleteItems) {
                phaseAction.markCompleted();
                List<ActionItem> phaseActionItems = actionItemRepository.findAllByPhaseActionId(phaseAction.getId());
                phaseActionItems.forEach(ActionItem::deactivate);
            }
        }
    }
}
