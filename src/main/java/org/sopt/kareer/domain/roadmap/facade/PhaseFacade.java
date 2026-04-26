package org.sopt.kareer.domain.roadmap.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.HomePhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.service.dto.response.PhaseActionDetail;
import org.sopt.kareer.domain.roadmap.service.phase.PhaseQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseFacade {

    private final PhaseQueryService phaseQueryService;

    public PhaseListResponse getPhases(Long memberId) {
        return PhaseListResponse.from(phaseQueryService.getPhases(memberId));
    }

    public RoadmapPhaseDetailResponse getRoadmapPhaseDetail(Long memberId, Long phaseId) {
        Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> raw =
                phaseQueryService.getRoadmapPhaseDetail(memberId, phaseId);

        Map<String, RoadmapPhaseDetailResponse.ActionGroupResponse> actions = new LinkedHashMap<>();
        actions.put("Visa", wrap(raw.get("Visa")));
        actions.put("Career", wrap(raw.get("Career")));
        actions.put("Done", wrap(raw.get("Done")));

        long totalCount = raw.values().stream().mapToLong(List::size).sum();
        return RoadmapPhaseDetailResponse.from(totalCount, actions);
    }

    public HomePhaseDetailResponse getHomePhaseDetail(Long memberId, Long phaseId) {
        List<PhaseActionDetail> details = phaseQueryService.getHomePhaseDetail(memberId, phaseId);
        List<HomePhaseDetailResponse.HomePhaseActionResponse> responses = details.stream()
                .map(d -> new HomePhaseDetailResponse.HomePhaseActionResponse(
                        d.id(), d.type(), d.title(), d.deadline()))
                .toList();
        return HomePhaseDetailResponse.from(responses);
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
}
