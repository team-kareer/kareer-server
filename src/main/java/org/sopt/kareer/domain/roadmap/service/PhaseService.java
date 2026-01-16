package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.*;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseService {

    private final MemberRepository memberRepository;
    private final PhaseRepository phaseRepository;

    public PhaseListResponse getPhases(Long memberId) {
        List<PhaseResponse> responses= phaseRepository.findPhases(memberId);

        return PhaseListResponse.from(responses);
    }

    public RoadmapPhaseDetailResponse getRoadmapPhaseDetail(Long phaseId) {
        if (!phaseRepository.existsById(phaseId)) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> raw =
                phaseRepository.getRoadmapPhaseDetail(phaseId);

        // Visa -> Career -> Done 순서로 순서 고정
        Map<String, RoadmapPhaseDetailResponse.ActionGroupResponse> actions = new LinkedHashMap<>();
        actions.put("Visa", wrap(raw.get("Visa")));
        actions.put("Career", wrap(raw.get("Career")));
        actions.put("Done", wrap(raw.get("Done")));

        // 전체 item 수
        long totalCount = raw.values().stream().mapToLong(List::size).sum();

        return RoadmapPhaseDetailResponse.from(totalCount, actions);
    }

    // items가 없는 경우 빈 리스트를 넣어주고, item 수를 count해주는 메서드
    private RoadmapPhaseDetailResponse.ActionGroupResponse wrap(
            List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse> items
    ) {
        if (items == null) items = List.of();
        return new RoadmapPhaseDetailResponse.ActionGroupResponse((long) items.size(), items);
    }

    public HomePhaseDetailResponse getHomePhaseDetail(Long phaseId) {
        if (!phaseRepository.existsById(phaseId)) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        List<HomePhaseActionResponse> actions = phaseRepository.getHomePhaseDetail(phaseId);

        return HomePhaseDetailResponse.from(actions);
    }
}
