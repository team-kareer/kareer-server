package org.sopt.kareer.domain.roadmap.service.phaseaction;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PhaseActionCommandService {

    private final PhaseActionRepository phaseActionRepository;
    private final ActionItemRepository actionItemRepository;

    public void createPhaseActionTodo(Long memberId, Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findByIdAndMemberIdAndRoadmapStatus(phaseActionId, memberId, RoadmapActiveStatus.ACTIVE)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        if (phaseAction.getAdded()) {
            throw new RoadMapException(RoadmapErrorCode.TODO_ALREADY_ADDED);
        }

        List<ActionItem> actionItems = actionItemRepository.findAllByPhaseActionId(phaseActionId);
        actionItems.forEach(ActionItem::activate);

        phaseAction.markAdded();
    }
}
