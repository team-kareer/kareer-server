package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PhaseRepositoryCustom {

    List<PhaseResponse> findPhases(@Param("memberId") Long memberId);
    Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> getRoadmapPhaseDetail(@Param("phaseId") Long phaseId);
}
