package org.sopt.kareer.domain.roadmap.entity.enums;

import org.sopt.kareer.domain.roadmap.exception.RoadMapException;

import java.util.Arrays;

import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_STATUS_BLANK;
import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_STATUS_INVALID;

public enum PhaseStatus {
    CURRENT,
    NEXT,
    FUTURE,
    PREVIOUS,
    PAST
    ;

    public static PhaseStatus from(String value){
        if (value == null || value.isBlank()){
            throw new RoadMapException(PHASE_STATUS_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RoadMapException(PHASE_STATUS_INVALID));
    }
}
