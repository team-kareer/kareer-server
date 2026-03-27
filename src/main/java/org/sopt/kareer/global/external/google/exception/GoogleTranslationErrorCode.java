package org.sopt.kareer.global.external.google.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum GoogleTranslationErrorCode implements ErrorCode {
    TRANSLATION_API_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Google Translate API 응답이 유효하지 않습니다."),
    ;

    private final int httpStatus;
    private final String message;

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
