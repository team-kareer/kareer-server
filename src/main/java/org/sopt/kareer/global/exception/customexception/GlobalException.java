package org.sopt.kareer.global.exception.customexception;

import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class GlobalException extends CustomException{
    public GlobalException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GlobalException(ErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
