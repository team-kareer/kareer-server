package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.*;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionTranslation;
import org.sopt.kareer.domain.roadmap.entity.PhaseTranslation;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionTranslationRepository;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseTranslationRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseService {

    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseTranslationRepository phaseTranslationRepository;
    private final PhaseActionTranslationRepository phaseActionTranslationRepository;

    public PhaseListResponse getPhases(Long memberId) {
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

        return PhaseListResponse.from(responses);
    }

    public RoadmapPhaseDetailResponse getRoadmapPhaseDetail(Long memberId, Long phaseId) {
        if (!phaseRepository.existsByIdAndRoadmap_Member_Id(phaseId, memberId)) {
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

        Map<String, RoadmapPhaseDetailResponse.ActionGroupResponse> actions = new LinkedHashMap<>();
        actions.put("Visa", wrap(raw.get("Visa")));
        actions.put("Career", wrap(raw.get("Career")));
        actions.put("Done", wrap(raw.get("Done")));

        long totalCount = raw.values().stream().mapToLong(List::size).sum();
        return RoadmapPhaseDetailResponse.from(totalCount, actions);
    }

    private RoadmapPhaseDetailResponse.ActionGroupResponse wrap(
            List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse> items
    ) {
        if (items == null) items = List.of();

        List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse> sorted = items.stream()
                .sorted(Comparator.comparing(RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse::deadline)
                        .thenComparing(RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse::title))
                .toList();

        return new RoadmapPhaseDetailResponse.ActionGroupResponse((long) sorted.size(), sorted);
    }

    public HomePhaseDetailResponse getHomePhaseDetail(Long memberId, Long phaseId) {
        if (!phaseRepository.existsByIdAndRoadmap_Member_Id(phaseId, memberId)) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        List<PhaseAction> phaseActions = phaseActionRepository.findByPhaseIdAndCompletedIsFalse(phaseId);

        String language = currentLanguage();
        List<Long> actionIds = phaseActions.stream().map(PhaseAction::getId).toList();
        Map<Long, PhaseActionTranslation> translationMap = phaseActionTranslationRepository
                .findAllByPhaseAction_IdInAndLanguage(actionIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getPhaseAction().getId(), t -> t));

        final Map<Long, PhaseActionTranslation> finalTranslationMap = translationMap;
        List<HomePhaseDetailResponse.HomePhaseActionResponse> actionResponses = phaseActions.stream()
                .map(pa -> {
                    PhaseActionTranslation t = finalTranslationMap.get(pa.getId());
                    if (t != null && t.getTitle() != null) {
                        return new HomePhaseDetailResponse.HomePhaseActionResponse(
                                pa.getId(), pa.getType().getDisplayName(), t.getTitle(), pa.getDeadline());
                    }
                    return HomePhaseDetailResponse.HomePhaseActionResponse.from(pa);
                })
                .toList();

        return HomePhaseDetailResponse.from(actionResponses);
    }

    private String currentLanguage() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }
}
