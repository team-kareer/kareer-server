package org.sopt.kareer.global.external.ai.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class AiException extends CustomException {
    public AiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AiException(ErrorCode errorCode){
      super(errorCode);
    }
}
