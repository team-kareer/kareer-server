package org.sopt.kareer.domain.phase.dto.response;

import java.util.List;

public record PhaseListResponse(
        List<PhaseResponse> phases
) {
    public static PhaseListResponse from(List<PhaseResponse> phases) {
        return new PhaseListResponse(phases);
    }
}
