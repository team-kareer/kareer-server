package org.sopt.kareer.global.auth.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class LoginException extends CustomException {

    public LoginException(LoginErrorCode errorCode) {
        super(errorCode);
    }

    public LoginException(LoginErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
