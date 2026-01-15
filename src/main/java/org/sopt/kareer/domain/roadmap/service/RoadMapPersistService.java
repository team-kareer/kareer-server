package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.entity.*;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Optional;

import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.INVALID_DATE_TYPE;

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
                    parseDate(phasePlan.startDate()),
                    parseDate(phasePlan.endDate())
            );
            Phase savedPhase = phaseRepository.save(phase);

            for(RoadmapResponse.PhaseActionPlan phaseActionPlan : Optional.ofNullable(phasePlan.actions()).orElse(Collections.emptyList())){
                PhaseAction phaseAction = PhaseAction.create(
                        phaseActionPlan.title(),
                        phaseActionPlan.description(),
                        PhaseActionType.from(phaseActionPlan.type()),
                        parseDate(phaseActionPlan.deadline()),
                        phaseActionPlan.importance(),
                        savedPhase
                );
                PhaseAction savedPhaseAction = phaseActionRepository.save(phaseAction);

                for(String g : Optional.ofNullable(phaseActionPlan.guideline()).orElse(Collections.emptyList())){
                    PhaseActionGuideline phaseActionGuideline = PhaseActionGuideline.create(g, savedPhaseAction);
                    phaseActionGuidelineRepository.save(phaseActionGuideline);
                }

                for(String c : Optional.ofNullable(phaseActionPlan.commonMistakes()).orElse(Collections.emptyList())){
                    PhaseActionMistake phaseActionMistake = PhaseActionMistake.create(c, savedPhaseAction);
                    phaseActionMistakeRepository.save(phaseActionMistake);
                }

                for(RoadmapResponse.ActionItemPlan actionItemPlan : Optional.ofNullable(phaseActionPlan.actionItems()).orElse(Collections.emptyList())){
                    ActionItem actionItem = ActionItem.create(
                            actionItemPlan.title(),
                            ActionItemType.from(actionItemPlan.actionsType()),
                            parseDate(actionItemPlan.deadline()),
                            member,
                            savedPhaseAction
                    );
                    actionItemRepository.save(actionItem);
                }

            }

        }
    }

    private LocalDate parseDate(String date) {
        try{
            return LocalDate.parse(date);
        } catch(DateTimeParseException e){
            throw new RoadMapException(INVALID_DATE_TYPE, e.getMessage());
        }
    }
}
