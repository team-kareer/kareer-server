package org.sopt.kareer.domain.actionitem.entity.enums;

import org.sopt.kareer.domain.phase.exception.PhaseErrorCode;
import org.sopt.kareer.domain.phase.exception.PhaseException;

import java.util.Arrays;

import static org.sopt.kareer.domain.phase.exception.PhaseErrorCode.*;

public enum ActionItemType {
    VISA,
    CAREER
    ;

    public static ActionItemType from(String value) {
        if(value == null || value.isBlank()){
            throw new PhaseException(ACTION_ITEM_TYPE_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new PhaseException(ACTION_ITEM_TYPE_INVALID));
    }
}
