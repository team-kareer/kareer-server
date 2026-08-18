package org.sopt.kareer.domain.roadmap.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.service.dto.response.AiGuideData;
import org.sopt.kareer.domain.roadmap.service.phaseaction.PhaseActionCommandService;
import org.sopt.kareer.domain.roadmap.service.phaseaction.PhaseActionQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseActionFacade {

    private final PhaseActionQueryService phaseActionQueryService;
    private final PhaseActionCommandService phaseActionCommandService;

    @Transactional
    public void createPhaseActionTodo(Long memberId, Long phaseActionId) {
        phaseActionCommandService.createPhaseActionTodo(memberId, phaseActionId);
    }

    public AiGuideResponse getAiGuide(Long memberId, Long phaseActionId) {
        AiGuideData data = phaseActionQueryService.getAiGuide(memberId, phaseActionId);
        return new AiGuideResponse(data.importance(), data.mistakes(), data.guidelines());
    }
}
