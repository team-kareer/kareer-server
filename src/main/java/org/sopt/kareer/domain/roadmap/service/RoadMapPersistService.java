package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.entity.phase.Phase;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseActionGuideline;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseActionMistake;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final RoadmapRepository roadmapRepository;

    @Transactional
    public RoadmapTranslationTarget saveRoadMap(Member member, RoadmapResponse response) {
        Roadmap roadmap = roadmapRepository.save(Roadmap.create(member));

        List<RoadmapTranslationTarget.PhaseTarget> phaseTargets = new ArrayList<>();

        for (RoadmapResponse.PhasePlan phasePlan : Optional.ofNullable(response.phases()).orElse(Collections.emptyList())) {
            Phase savedPhase = phaseRepository.save(Phase.create(
                    roadmap,
                    phasePlan.sequence(),
                    phasePlan.goal(),
                    phasePlan.description(),
                    PhaseStatus.from(phasePlan.status()),
                    parseDate(phasePlan.startDate()),
                    parseDate(phasePlan.endDate())
            ));

            List<RoadmapTranslationTarget.PhaseActionTarget> actionTargets = new ArrayList<>();

            for (RoadmapResponse.PhaseActionPlan phaseActionPlan : Optional.ofNullable(phasePlan.actions()).orElse(Collections.emptyList())) {
                PhaseAction savedPhaseAction = phaseActionRepository.save(PhaseAction.create(
                        phaseActionPlan.title(),
                        phaseActionPlan.description(),
                        PhaseActionType.from(phaseActionPlan.type()),
                        parseDate(phaseActionPlan.deadline()),
                        phaseActionPlan.importance(),
                        savedPhase
                ));

                List<RoadmapTranslationTarget.GuidelineTarget> guidelineTargets = new ArrayList<>();
                for (String g : Optional.ofNullable(phaseActionPlan.guideline()).orElse(Collections.emptyList())) {
                    PhaseActionGuideline saved = phaseActionGuidelineRepository.save(PhaseActionGuideline.create(g, savedPhaseAction));
                    guidelineTargets.add(new RoadmapTranslationTarget.GuidelineTarget(saved.getId(), g));
                }

                List<RoadmapTranslationTarget.MistakeTarget> mistakeTargets = new ArrayList<>();
                for (String c : Optional.ofNullable(phaseActionPlan.commonMistakes()).orElse(Collections.emptyList())) {
                    PhaseActionMistake saved = phaseActionMistakeRepository.save(PhaseActionMistake.create(c, savedPhaseAction));
                    mistakeTargets.add(new RoadmapTranslationTarget.MistakeTarget(saved.getId(), c));
                }

                List<RoadmapTranslationTarget.ActionItemTarget> actionItemTargets = new ArrayList<>();
                for (RoadmapResponse.ActionItemPlan actionItemPlan : Optional.ofNullable(phaseActionPlan.actionItems()).orElse(Collections.emptyList())) {
                    ActionItem saved = actionItemRepository.save(ActionItem.create(
                            actionItemPlan.title(),
                            ActionItemType.from(actionItemPlan.actionsType()),
                            parseDate(actionItemPlan.deadline()),
                            member,
                            savedPhaseAction
                    ));
                    actionItemTargets.add(new RoadmapTranslationTarget.ActionItemTarget(saved.getId(), actionItemPlan.title()));
                }

                actionTargets.add(new RoadmapTranslationTarget.PhaseActionTarget(
                        savedPhaseAction.getId(),
                        phaseActionPlan.title(),
                        phaseActionPlan.description(),
                        phaseActionPlan.importance(),
                        guidelineTargets, mistakeTargets, actionItemTargets));
            }

            phaseTargets.add(new RoadmapTranslationTarget.PhaseTarget(
                    savedPhase.getId(), phasePlan.goal(), phasePlan.description(), actionTargets));
        }

        return new RoadmapTranslationTarget(phaseTargets);
    }

    private LocalDate parseDate(String date) {
        try{
            return LocalDate.parse(date);
        } catch(DateTimeParseException e){
            throw new RoadMapException(INVALID_DATE_TYPE, e.getMessage());
        }
    }
}
