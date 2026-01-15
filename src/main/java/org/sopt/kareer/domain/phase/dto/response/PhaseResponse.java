package org.sopt.kareer.domain.phase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.kareer.domain.phase.entity.enums.PhaseStatus;

import java.time.LocalDate;

@Schema(description = "Phase 리스트 응답")
public record PhaseResponse(
        @Schema(description = "Phase id", example = "1")
        Long phaseId,

        @Schema(description = "Phase 상태", example = "Current")
        String phaseStatus,

        @Schema(description = "Phase 순서", example = "1")
        Integer sequence,

        @Schema(description = "Phase 목표", example = "Verify Requirements")
        String goal,

        @Schema(description = "Phase 설명", example = "Initial setup and documentation")
        String description,

        @Schema(description = "Work 상태", example = "Remained works")
        String workStatus,

        @Schema(description = "Work 수(Phase 상태에 따라 남은 work 수, 계획된 work 수)", example = "3")
        Long worksCount,

        @Schema(description = "Phase 시작일", example = "2025-09-01")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate startDate,

        @Schema(description = "Phase 종료일", example = "2025-12-01")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate endDate
) {
        @QueryProjection
        public PhaseResponse(
                Long phaseId,
                PhaseStatus status,
                Integer sequence,
                String goal,
                String description,
                Long worksCount,
                LocalDate startDate,
                LocalDate endDate
        ) {
                this(
                        phaseId,
                        status.getDisplayName(),
                        sequence,
                        goal,
                        description,
                        PhaseStatus.determineWorkStatus(status, worksCount),
                        worksCount,
                        startDate,
                        endDate
                );
        }
}