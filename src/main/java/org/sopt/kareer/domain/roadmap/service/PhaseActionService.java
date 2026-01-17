package org.sopt.kareer.domain.roadmap.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseActionService {

    private final PhaseActionRepository phaseActionRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public void createPhaseActionTodo(Long memberId, Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findById(phaseActionId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        if(phaseAction.getAdded()){
        throw new RoadMapException(RoadmapErrorCode.TODO_ALREADY_ADDED);
        }

        if(!phaseAction.getPhase().getMember().getId().equals(memberId)){
            throw new RoadMapException(RoadmapErrorCode.PHASE_ACTION_FORBIDDEN);
        }

        List<ActionItem> actionItems = actionItemRepository.findAllByPhaseActionId(phaseActionId);

        for (ActionItem actionItem : actionItems) {
            actionItem.activate();
        }

        phaseAction.markAdded();
    }
}
