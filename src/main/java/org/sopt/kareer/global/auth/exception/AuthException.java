package org.sopt.kareer.global.auth.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class AuthException extends CustomException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
