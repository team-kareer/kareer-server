package org.sopt.kareer.domain.roadmap.dto.response;

import org.sopt.kareer.domain.roadmap.entity.PhaseAction;

import java.util.List;

public record AiGuideResponse(
        String importance,
        List<String> mistakes,
        List<String> guidelines
) {
    public static AiGuideResponse from(PhaseAction phaseAction, List<String> mistakes, List<String> guidelines) {
        return new AiGuideResponse(phaseAction.getImportance(), mistakes, guidelines);
    }
}
