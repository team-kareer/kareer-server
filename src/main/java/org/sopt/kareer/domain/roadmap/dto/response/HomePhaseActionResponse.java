package org.sopt.kareer.domain.roadmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;

import java.time.LocalDate;

@Schema(description = "홈 Phase Action 조회 응답")
public record HomePhaseActionResponse(
        @Schema(description = "Phase Action 타입", example="Visa")
        String type,

        @Schema(description = "Phase Action 제목", example="Prepare OPT application documents")
        String title,

        @Schema(description = "Phase Action 마감기한", example="2026-01-01")
        LocalDate deadline
) {
        private static String formatType(PhaseActionType type) {
                String lowerCase = type.name().toLowerCase();
                return lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
        }

        public HomePhaseActionResponse(PhaseActionType type, String title, LocalDate deadline) {
                this(formatType(type), title, deadline);
        }
}
