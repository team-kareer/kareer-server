package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "홈 Phase 리스트 조회 응답")
public record HomePhaseDetailResponse(
        @Schema(description = "Phase의 PhaseAction 수", example = "1")
        Long count,

        @Schema(description = "Phase의 PhaseAction 리스트")
        List<HomePhaseActionResponse> actions
) {
    public record HomePhaseActionResponse(
            @Schema(description = "Phase Action 고유번호", example="1")
            Long id,

            @Schema(description = "Phase Action 타입", example="Visa")
            String type,

            @Schema(description = "Phase Action 제목", example="Prepare OPT application documents")
            String title,

            @Schema(description = "Phase Action 마감기한", example="2026-01-01")
            LocalDate deadline
    ) {
        public static HomePhaseActionResponse from(PhaseAction phaseAction) {
            return new HomePhaseActionResponse(
                    phaseAction.getId(),
                    phaseAction.getType().getDisplayName(),
                    phaseAction.getTitle(),
                    phaseAction.getDeadline()
            );
        }
    }

    public static HomePhaseDetailResponse from(List<HomePhaseActionResponse> actions) {
        return new HomePhaseDetailResponse((long) actions.size(), actions);
    }
}