package org.sopt.kareer.global.external.ai.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum LlmErrorCode implements ErrorCode {
    LLM_JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LLM 응답 JSON 파싱에 실패했습니다."),
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
