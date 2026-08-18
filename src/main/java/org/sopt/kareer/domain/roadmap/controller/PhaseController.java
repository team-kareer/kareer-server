package org.sopt.kareer.domain.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.HomePhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.facade.PhaseFacade;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap/phases")
public class PhaseController implements PhaseApi {

    private final PhaseFacade phaseFacade;

    @Override
    public ResponseEntity<BaseResponse<PhaseListResponse>> getPhaseList(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(phaseFacade.getPhases(memberId), "Phase 리스트가 조회되었습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<RoadmapPhaseDetailResponse>> getRoadmapPhaseDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(phaseFacade.getRoadmapPhaseDetail(memberId, phaseId), "로드맵 Phase 상세정보가 조회되었습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<HomePhaseDetailResponse>> getHomePhaseDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(phaseFacade.getHomePhaseDetail(memberId, phaseId), "홈 Phase 상세정보가 조회되었습니다."));
    }
}
