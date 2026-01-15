package org.sopt.kareer.domain.roadmap.entity.enums;

import org.sopt.kareer.domain.roadmap.exception.RoadMapException;

import java.util.Arrays;

import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.ACTION_ITEM_TYPE_BLANK;
import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.ACTION_ITEM_TYPE_INVALID;

public enum ActionItemType {
    VISA,
    CAREER
    ;

    public static ActionItemType from(String value) {
        if(value == null || value.isBlank()){
            throw new RoadMapException(ACTION_ITEM_TYPE_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RoadMapException(ACTION_ITEM_TYPE_INVALID));
    }
}
