package org.sopt.kareer.domain.roadmap.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.StringPath;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record RoadmapPhaseDetailResponse(
        Long totalCount,
        Map<String, ActionGroupResponse> actions
) {
    public record ActionGroupResponse(
        Long count,
        List<ActionResponse> items
    ) {
        public record ActionResponse(
                String title,
                String description,
                LocalDate deadline
        ) {
            @QueryProjection
            public ActionResponse { }
        }
    }
}
