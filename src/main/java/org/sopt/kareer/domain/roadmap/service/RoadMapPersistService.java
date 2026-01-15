package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.entity.*;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoadMapPersistService {

    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionGuidelineRepository phaseActionGuidelineRepository;
    private final PhaseActionMistakeRepository phaseActionMistakeRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public void saveRoadMap(Member member, RoadmapResponse response) {

        for(RoadmapResponse.PhasePlan phasePlan : Optional.ofNullable(response.phases()).orElse(Collections.emptyList())){
            Phase phase = Phase.create(
                    member,
                    phasePlan.sequence(),
                    phasePlan.goal(),
                    phasePlan.description(),
                    PhaseStatus.from(phasePlan.status()),
                    LocalDate.parse(phasePlan.startDate()),
                    LocalDate.parse(phasePlan.endDate())
            );
            Phase savedPhase = phaseRepository.save(phase);

            for(RoadmapResponse.PhaseActionPlan phaseActionPlan : phasePlan.actions()){
                PhaseAction phaseAction = PhaseAction.create(
                        phaseActionPlan.title(),
                        phaseActionPlan.description(),
                        PhaseActionType.from(phaseActionPlan.type()),
                        LocalDate.parse(phaseActionPlan.deadline()),
                        phaseActionPlan.importance(),
                        savedPhase
                );
                PhaseAction savedPhaseAction = phaseActionRepository.save(phaseAction);

                for(String g : phaseActionPlan.guideline()){
                    PhaseActionGuideline phaseActionGuideline = PhaseActionGuideline.create(g, savedPhaseAction);
                    phaseActionGuidelineRepository.save(phaseActionGuideline);
                }

                for(String c : phaseActionPlan.commonMistakes()){
                    PhaseActionMistake phaseActionMistake = PhaseActionMistake.create(c, savedPhaseAction);
                    phaseActionMistakeRepository.save(phaseActionMistake);
                }

                for(RoadmapResponse.ActionItemPlan actionItemPlan : phaseActionPlan.actionItems()){
                    ActionItem actionItem = ActionItem.create(
                            actionItemPlan.title(),
                            ActionItemType.from(actionItemPlan.actionsType()),
                            LocalDate.parse(actionItemPlan.deadline()),
                            member,
                            savedPhaseAction
                    );
                    actionItemRepository.save(actionItem);
                }

            }

        }
    }
}
