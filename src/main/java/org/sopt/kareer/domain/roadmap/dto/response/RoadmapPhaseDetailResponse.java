package org.sopt.kareer.domain.roadmap.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "로드맵 Phase 상세 조회 응답")
public record RoadmapPhaseDetailResponse(
        @Schema(description = "Phase의 PhaseAction 수", example = "1")
        Long totalCount,

        @Schema(description = "Phase에 해당하는 그룹화된 PhaseAction")
        Map<String, ActionGroupResponse> actions
) {
    public record ActionGroupResponse(
        @Schema(description = "태그(Visa, Career, Done)에 해당하는 PhaseAction 수", example = "1")
        Long count,

        @Schema(description = "태그(Visa, Career, Done)에 해당하는 PhaseAction 리스트")
        List<ActionResponse> items
    ) {
        public record ActionResponse(
                @Schema(description = "Phase Action 고유번호", example="1")
                Long phaseActionId,

                @Schema(description = "Phase Action 제목", example = "Prepare internship log")
                String title,

                @Schema(description = "Phase Action 설명", example = "Document all work experience during internship period")
                String description,

                @Schema(description = "Phase Action 마감 기한", example = "2026-01-24")
                LocalDate deadline,

                @Schema(description = "Phase Action todo 추가 여부", example = "true")
                boolean added
        ) {
            @QueryProjection
            public ActionResponse { }
        }
    }

    public static RoadmapPhaseDetailResponse from(Long count,Map<String, RoadmapPhaseDetailResponse.ActionGroupResponse> phase) {
        return new RoadmapPhaseDetailResponse(count, phase);
    }
}
