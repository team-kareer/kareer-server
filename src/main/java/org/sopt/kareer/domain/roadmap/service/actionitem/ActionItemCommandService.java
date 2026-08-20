package org.sopt.kareer.domain.roadmap.service.actionitem;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemTranslationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionItemCommandService {

    private final ActionItemRepository actionItemRepository;
    private final ActionItemTranslationRepository actionItemTranslationRepository;

    public ActionItem createActionItem(
            Member member,
            ActionItemType type,
            String title,
            LocalDate deadline
    ) {
        return actionItemRepository.save(
                ActionItem.createUserActionItem(title, type, deadline, member)
        );
    }

    public ActionItem updateActionItem(
            Long memberId,
            Long actionItemId,
            String title,
            LocalDate deadline
    ) {
        ActionItem actionItem = getActionItem(memberId, actionItemId);

        if (title != null) {
            actionItemTranslationRepository.deleteAllByActionItem_Id(actionItemId);
        }
        actionItem.update(title, deadline);
        return actionItem;
    }

    public void deleteActionItem(Long memberId, Long actionItemId) {
        ActionItem actionItem = getActionItem(memberId, actionItemId);
        actionItemTranslationRepository.deleteAllByActionItem_Id(actionItemId);
        actionItemRepository.delete(actionItem);
    }

    public void toggleCompletion(Long memberId, Long actionItemId) {
        ActionItem actionItem = getActionItem(memberId, actionItemId);

        if (actionItem.getStatus() == ActionItemStatus.INACTIVE) {
            throw new RoadMapException(RoadmapErrorCode.ACTION_ITEM_INACTIVE);
        }

        PhaseAction phaseAction = actionItem.getPhaseAction();

        if (phaseAction == null) {
            actionItem.toggleCompletion();
            return;
        }

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

    private ActionItem getActionItem(Long memberId, Long actionItemId) {
        return actionItemRepository.findByIdAndMemberId(actionItemId, memberId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.ACTION_ITEM_NOT_FOUND));
    }
}
