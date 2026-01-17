package org.sopt.kareer.domain.roadmap.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;

import java.util.Arrays;

import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_ACTION_TYPE_BLANK;
import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_ACTION_TYPE_INVALID;

@Getter
@AllArgsConstructor
public enum PhaseActionType {
    VISA("Visa"),
    CAREER("Career")
    ;

    private final String displayName;

    public static PhaseActionType from(String value){
        if(value == null || value.isBlank()){
            throw new RoadMapException(PHASE_ACTION_TYPE_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RoadMapException(PHASE_ACTION_TYPE_INVALID));
    }
}
