package org.sopt.kareer.global.external.google.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class GoogleTranslationException extends CustomException {

    public GoogleTranslationException(GoogleTranslationErrorCode errorCode) {
        super(errorCode);
    }

    public GoogleTranslationException(GoogleTranslationErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
