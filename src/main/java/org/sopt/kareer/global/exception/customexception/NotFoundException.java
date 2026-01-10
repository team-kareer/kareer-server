package org.sopt.kareer.global.exception.customexception;

import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
