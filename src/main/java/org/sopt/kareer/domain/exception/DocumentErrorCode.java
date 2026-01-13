package org.sopt.kareer.domain.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DocumentErrorCode implements ErrorCode {
    EXTRACT_TEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 파일에서 텍스트 추출에 실패하였습니다."),
    DOCUMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "문서 임베딩 중 오류가 발생했습니다."),
    FILE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 생성 중 오류가 발생했습니다."),
    DOCUMENT_HASH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 해시 계산에 실패했습니다."),
    DOCUMENT_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 존재하는 문서입니다."),
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
