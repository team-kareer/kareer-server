package org.sopt.kareer.global.exception.customexception;

import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class BadRequestException extends CustomException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(ErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
