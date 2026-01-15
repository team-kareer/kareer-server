package org.sopt.kareer.domain.phase.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class PhaseException extends CustomException {
    public PhaseException(PhaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PhaseException(PhaseErrorCode errorCode){
        super(errorCode);
    }

}
