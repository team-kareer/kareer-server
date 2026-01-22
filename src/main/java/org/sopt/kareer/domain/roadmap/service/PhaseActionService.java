package org.sopt.kareer.domain.roadmap.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseActionService {

    private final PhaseActionRepository phaseActionRepository;
    private final ActionItemRepository actionItemRepository;
    private final PhaseActionMistakeRepository mistakeRepository;
    private final PhaseActionGuidelineRepository guidelineRepository;

    @Transactional
    public void createPhaseActionTodo(Long memberId, Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findByIdAndPhase_Member_Id(phaseActionId, memberId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        if(phaseAction.getAdded()){
            throw new RoadMapException(RoadmapErrorCode.TODO_ALREADY_ADDED);
        }

        List<ActionItem> actionItems = actionItemRepository.findAllByPhaseActionId(phaseActionId);

        for (ActionItem actionItem : actionItems) {
            actionItem.activate();
        }

        phaseAction.markAdded();
    }

    public AiGuideResponse getAiGuide(Long memberId, Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findByIdAndPhase_Member_Id(phaseActionId, memberId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        List<String> mistakes = mistakeRepository.findContentByPhaseActionId(phaseActionId);
        List<String> guidelines = guidelineRepository.findContentByPhaseActionId(phaseActionId);

        return AiGuideResponse.from(phaseAction, mistakes, guidelines);
    }
}
