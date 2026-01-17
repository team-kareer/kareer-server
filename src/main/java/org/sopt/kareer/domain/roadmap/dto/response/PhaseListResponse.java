package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Phase 리스트 응답")
public record PhaseListResponse(
        @Schema(description = "Phase 리스트 응답")
        List<PhaseResponse> phases
) {
    public static PhaseListResponse from(List<PhaseResponse> phases) {
        return new PhaseListResponse(phases);
    }
}
