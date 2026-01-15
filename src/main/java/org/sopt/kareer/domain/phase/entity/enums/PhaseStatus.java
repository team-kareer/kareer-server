package org.sopt.kareer.domain.phase.entity.enums;

import org.sopt.kareer.domain.phase.exception.PhaseErrorCode;
import org.sopt.kareer.domain.phase.exception.PhaseException;

import java.util.Arrays;

public enum PhaseStatus {
    CURRENT,
    NEXT,
    FUTURE,
    PREVIOUS,
    PAST;

    public static PhaseStatus from(String value){
        if (value == null || value.isBlank()){
            throw new PhaseException(PhaseErrorCode.PHASE_STATUS_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new PhaseException(PhaseErrorCode.PHASE_STATUS_INVALID));
    }
}
