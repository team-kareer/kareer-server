package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionTranslation;
import org.sopt.kareer.domain.roadmap.entity.PhaseTranslation;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseTranslationRepository;
import org.sopt.kareer.domain.roadmap.service.dto.response.PhaseActionDetail;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseQueryService {

    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseTranslationRepository phaseTranslationRepository;
    private final PhaseActionTranslationRepository phaseActionTranslationRepository;

    public List<PhaseResponse> getPhases(Long memberId) {
        List<PhaseResponse> responses = phaseRepository.findPhases(memberId);

        String language = currentLanguage();
        List<Long> phaseIds = responses.stream().map(PhaseResponse::phaseId).toList();
        Map<Long, PhaseTranslation> translationMap = phaseTranslationRepository
                .findAllByPhase_IdInAndLanguage(phaseIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getPhase().getId(), t -> t));

        if (!translationMap.isEmpty()) {
            responses = responses.stream()
                    .map(r -> {
                        PhaseTranslation t = translationMap.get(r.phaseId());
                        if (t == null) return r;
                        return new PhaseResponse(r.phaseId(), r.phaseStatus(), r.sequence(),
                                t.getGoal(), t.getDescription(), r.workStatus(), r.worksCount(),
                                r.startDate(), r.endDate());
                    })
                    .toList();
        }

        return responses;
    }

    public Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> getRoadmapPhaseDetail(Long memberId, Long phaseId) {
        if (!phaseRepository.existsByIdAndRoadmap_Member_IdAndRoadmap_Status(phaseId, memberId, RoadmapActiveStatus.ACTIVE)) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> raw =
                phaseRepository.getRoadmapPhaseDetail(phaseId);

        String language = currentLanguage();
        List<Long> actionIds = raw.values().stream()
                .flatMap(List::stream)
                .map(RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse::phaseActionId)
                .toList();
        Map<Long, PhaseActionTranslation> translationMap = phaseActionTranslationRepository
                .findAllByPhaseAction_IdInAndLanguage(actionIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getPhaseAction().getId(), t -> t));

        if (!translationMap.isEmpty()) {
            raw = raw.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream()
                                    .map(r -> {
                                        PhaseActionTranslation t = translationMap.get(r.phaseActionId());
                                        if (t == null || t.getTitle() == null) return r;
                                        return new RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse(
                                                r.phaseActionId(), t.getTitle(), t.getDescription(),
                                                r.deadline(), r.added());
                                    })
                                    .toList(),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        }

        return raw;
    }

    public List<PhaseActionDetail> getHomePhaseDetail(Long memberId, Long phaseId) {
        if (!phaseRepository.existsByIdAndRoadmap_Member_IdAndRoadmap_Status(phaseId, memberId, RoadmapActiveStatus.ACTIVE)) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        List<PhaseAction> phaseActions = phaseActionRepository.findByPhaseIdAndCompletedIsFalse(phaseId);

        String language = currentLanguage();
        List<Long> actionIds = phaseActions.stream().map(PhaseAction::getId).toList();
        Map<Long, PhaseActionTranslation> translationMap = phaseActionTranslationRepository
                .findAllByPhaseAction_IdInAndLanguage(actionIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getPhaseAction().getId(), t -> t));

        return phaseActions.stream()
                .map(pa -> {
                    PhaseActionTranslation t = translationMap.get(pa.getId());
                    String title = (t != null && t.getTitle() != null) ? t.getTitle() : pa.getTitle();
                    return new PhaseActionDetail(pa.getId(), pa.getType().getDisplayName(), title, pa.getDeadline());
                })
                .toList();
    }

    private String currentLanguage() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }
}
