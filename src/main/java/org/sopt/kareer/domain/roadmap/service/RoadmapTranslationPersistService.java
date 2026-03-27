package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.domain.roadmap.entity.*;
import org.sopt.kareer.domain.roadmap.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapTranslationPersistService {

    private final PhaseTranslationRepository phaseTranslationRepository;
    private final PhaseActionTranslationRepository phaseActionTranslationRepository;
    private final PhaseActionGuidelineTranslationRepository guidelineTranslationRepository;
    private final PhaseActionMistakeTranslationRepository mistakeTranslationRepository;
    private final ActionItemTranslationRepository actionItemTranslationRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionGuidelineRepository guidelineRepository;
    private final PhaseActionMistakeRepository mistakeRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public void saveTranslations(RoadmapTranslationTarget target, String language) {
        List<Long> phaseIds = target.phases().stream()
                .map(RoadmapTranslationTarget.PhaseTarget::phaseId).toList();
        List<Long> actionIds = target.phases().stream()
                .flatMap(p -> p.actions().stream())
                .map(RoadmapTranslationTarget.PhaseActionTarget::phaseActionId).toList();
        List<Long> actionItemIds = target.phases().stream()
                .flatMap(p -> p.actions().stream())
                .flatMap(a -> a.actionItems().stream())
                .map(RoadmapTranslationTarget.ActionItemTarget::actionItemId).toList();

        if (!actionItemIds.isEmpty()) actionItemTranslationRepository.deleteAllByActionItem_IdInAndLanguage(actionItemIds, language);
        if (!actionIds.isEmpty()) {
            guidelineTranslationRepository.deleteAllByGuideline_PhaseAction_IdInAndLanguage(actionIds, language);
            mistakeTranslationRepository.deleteAllByMistake_PhaseAction_IdInAndLanguage(actionIds, language);
            phaseActionTranslationRepository.deleteAllByPhaseAction_IdInAndLanguage(actionIds, language);
        }
        if (!phaseIds.isEmpty()) phaseTranslationRepository.deleteAllByPhase_IdInAndLanguage(phaseIds, language);

        for (RoadmapTranslationTarget.PhaseTarget phaseTarget : target.phases()) {
            Phase phase = phaseRepository.getReferenceById(phaseTarget.phaseId());
            phaseTranslationRepository.save(
                    PhaseTranslation.create(phase, language, phaseTarget.goal(), phaseTarget.description()));

            for (RoadmapTranslationTarget.PhaseActionTarget actionTarget : phaseTarget.actions()) {
                PhaseAction phaseAction = phaseActionRepository.getReferenceById(actionTarget.phaseActionId());
                phaseActionTranslationRepository.save(
                        PhaseActionTranslation.create(phaseAction, language,
                                actionTarget.title(), actionTarget.description(), actionTarget.importance()));

                for (RoadmapTranslationTarget.GuidelineTarget g : actionTarget.guidelines()) {
                    PhaseActionGuideline guideline = guidelineRepository.getReferenceById(g.guidelineId());
                    guidelineTranslationRepository.save(
                            PhaseActionGuidelineTranslation.create(guideline, language, g.content()));
                }

                for (RoadmapTranslationTarget.MistakeTarget m : actionTarget.mistakes()) {
                    PhaseActionMistake mistake = mistakeRepository.getReferenceById(m.mistakeId());
                    mistakeTranslationRepository.save(
                            PhaseActionMistakeTranslation.create(mistake, language, m.content()));
                }

                for (RoadmapTranslationTarget.ActionItemTarget item : actionTarget.actionItems()) {
                    ActionItem actionItem = actionItemRepository.getReferenceById(item.actionItemId());
                    actionItemTranslationRepository.save(
                            ActionItemTranslation.create(actionItem, language, item.title()));
                }
            }
        }
    }
}
