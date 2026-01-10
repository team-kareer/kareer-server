package org.sopt.kareer.global.exception.customexception;

import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
