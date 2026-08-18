package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.kareer.domain.roadmap.dto.response.HomePhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Phase API", description = "Phase 관련 API")
public interface PhaseApi {

    @Operation(summary = "Phase 리스트 조회", description = "Phase 리스트를 조회합니다.")
    @GetMapping
    ResponseEntity<BaseResponse<PhaseListResponse>> getPhaseList(@AuthenticationPrincipal Long memberId);

    @Operation(summary = "로드맵 Phase 상세정보 조회", description = "로드맵 Phase 상세조회를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.ROADMAP_PHASE_LIST_DETAIL)
    @GetMapping("/{phaseId}")
    ResponseEntity<BaseResponse<RoadmapPhaseDetailResponse>> getRoadmapPhaseDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseId);

    @Operation(summary = "홈 Phase 상세정보 조회", description = "홈 Phase 상세조회를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.HOME_PHASE_LIST_DETAIL)
    @GetMapping("/{phaseId}/home")
    ResponseEntity<BaseResponse<HomePhaseDetailResponse>> getHomePhaseDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseId);
}
