package org.sopt.kareer.global.external.clova.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class ClovaException extends CustomException {
    public ClovaException(ClovaErrorCode errorCode) {
        super(errorCode);
    }

    public ClovaException(ClovaErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
