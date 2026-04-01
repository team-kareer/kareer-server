package org.sopt.kareer.global.external.clova.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ClovaErrorCode implements ErrorCode {
    EXTRACT_IMAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지로부터 텍스트를 추출하는데 실패했습니다.")
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
