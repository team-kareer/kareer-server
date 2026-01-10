package org.sopt.kareer.global.exception.customexception;

import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class InternalServerException extends CustomException{
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InternalServerException(ErrorCode errorCode, final String messageDetail) {
        super(errorCode, messageDetail);
    }
}
