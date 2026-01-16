package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "홈 Phase 리스트 조회 응답")
public record HomePhaseDetailResponse(
        @Schema(description = "Phase의 PhaseAction 수", example = "1")
        Long count,

        @Schema(description = "Phase의 PhaseAction 리스트")
        List<HomePhaseActionResponse> actions
) {
    public static HomePhaseDetailResponse from(List<HomePhaseActionResponse> actions) {
        return new HomePhaseDetailResponse((long) actions.size(), actions);
    }
}