package org.sopt.kareer.global.external.ai.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class RagException extends CustomException {
    public RagException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RagException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
