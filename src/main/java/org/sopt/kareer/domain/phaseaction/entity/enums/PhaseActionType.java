package org.sopt.kareer.domain.phaseaction.entity.enums;

import org.sopt.kareer.domain.phase.exception.PhaseErrorCode;
import org.sopt.kareer.domain.phase.exception.PhaseException;

import java.util.Arrays;

public enum PhaseActionType {
    VISA,
    CAREER,
    URGENT
    ;

    public static PhaseActionType from(String value){
        if(value == null || value.isBlank()){
            throw new PhaseException(PhaseErrorCode.PHASE_ACTION_TYPE_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new PhaseException(PhaseErrorCode.PHASE_ACTION_TYPE_INVALID, value));
    }
}
