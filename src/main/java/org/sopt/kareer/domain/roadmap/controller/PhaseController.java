package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.service.PhaseService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Phase API", description = "Phase 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phases")
public class PhaseController {

    private final PhaseService phaseService;

    @GetMapping
    @Operation(summary = "Phase 리스트 조회", description = "Phase 리스트를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.PHASE_LIST)
    public ResponseEntity<BaseResponse<PhaseListResponse>> getPhaseList(
            @AuthenticationPrincipal Long memberId
    ) {
        PhaseListResponse response = phaseService.getPhases(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(response, "Phase 리스트가 조회되었습니다.")
        );
    }

    @GetMapping("/{phaseId}/roadmap")
    @Operation(summary = "로드맵 Phase 상세정보 조회", description = "로드맵 Phase 리스트를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.ROADMAP_LIST_DETAIL)
    public ResponseEntity<BaseResponse<RoadmapPhaseDetailResponse>> getRoadmapPhaseDetail(@PathVariable Long phaseId) {
        RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(phaseId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(response, "Phase 리스트가 조회되었습니다.")
                );
    }
}
