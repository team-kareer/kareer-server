package org.sopt.kareer.global.document.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DocumentErrorCode implements ErrorCode {
    EXTRACT_TEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 파일에서 텍스트 추출에 실패하였습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 이미지 파일입니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST.value(), "파일이 비어있습니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 파일 형식입니다."),
    EXTRACT_IMAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 추출에 실패하였습니다."),
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
