package org.sopt.kareer.global.exception.errorcode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    FILE_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(),"파일 초기화에 실패하였습니다."),
    FILE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 갱신에 실패하였습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "유효하지 않은 Http 메서드입니다."),
    NOT_FOUND_PATH(NOT_FOUND.value(), "존재하지 않는 API 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST.value(), "JSON 형식이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST.value(), "필수 요청 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST.value(), "요청 파라미터 타입이 일치하지 않습니다."),
    MISSING_PATH_VARIABLE(HttpStatus.BAD_REQUEST.value(), "경로 변수 값이 누락되었습니다.")
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
