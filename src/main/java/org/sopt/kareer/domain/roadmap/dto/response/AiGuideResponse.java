package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;

import java.util.List;

@Schema(description = "AI 가이드 응답")
public record AiGuideResponse(
        @Schema(description = "AI guide - Why this matters")
        String importance,

        @Schema(description = "AI guide - How to do it properly")
        List<String> mistakes,

        @Schema(description = "AI guide - Common mistakes")
        List<String> guidelines
) {
    public static AiGuideResponse from(PhaseAction phaseAction, List<String> mistakes, List<String> guidelines) {
        return new AiGuideResponse(phaseAction.getImportance(), mistakes, guidelines);
    }
}
