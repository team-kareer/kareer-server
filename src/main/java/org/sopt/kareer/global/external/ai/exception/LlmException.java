package org.sopt.kareer.global.external.ai.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class LlmException extends CustomException {

    public LlmException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public LlmException(ErrorCode errorCode){
        super(errorCode);
    }
}
