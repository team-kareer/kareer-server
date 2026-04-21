package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionTranslation;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionTranslationRepository;
import org.sopt.kareer.domain.roadmap.service.dto.response.AiGuideData;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseActionQueryService {

    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionGuidelineRepository guidelineRepository;
    private final PhaseActionMistakeRepository mistakeRepository;
    private final PhaseActionTranslationRepository phaseActionTranslationRepository;
    private final PhaseActionGuidelineTranslationRepository guidelineTranslationRepository;
    private final PhaseActionMistakeTranslationRepository mistakeTranslationRepository;

    public AiGuideData getAiGuide(Long memberId, Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findByIdAndMemberIdAndRoadmapStatus(phaseActionId, memberId, RoadmapActiveStatus.ACTIVE)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        String importance = phaseAction.getImportance();
        List<String> guidelines = guidelineRepository.findContentByPhaseActionId(phaseActionId);
        List<String> mistakes = mistakeRepository.findContentByPhaseActionId(phaseActionId);

        String language = LocaleContextHolder.getLocale().toLanguageTag();
        importance = phaseActionTranslationRepository.findByPhaseAction_IdAndLanguage(phaseActionId, language)
                .filter(t -> t.getImportance() != null)
                .map(PhaseActionTranslation::getImportance)
                .orElse(importance);

        List<String> translatedGuidelines =
                guidelineTranslationRepository.findContentByPhaseActionIdAndLanguage(phaseActionId, language);
        if (!translatedGuidelines.isEmpty()) guidelines = translatedGuidelines;

        List<String> translatedMistakes =
                mistakeTranslationRepository.findContentByPhaseActionIdAndLanguage(phaseActionId, language);
        if (!translatedMistakes.isEmpty()) mistakes = translatedMistakes;

        return new AiGuideData(importance, guidelines, mistakes);
    }
}
