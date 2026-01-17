package org.sopt.kareer.global.external.ai.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum RagErrorCode implements ErrorCode {
    EXTRACT_TEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 파일에서 텍스트 추출에 실패하였습니다."),
    EMBEDDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 업로드 및 임베딩에 실패했습니다."),
    DOCUMENTS_RETRIEVED_EMPTY(HttpStatus.NOT_FOUND.value(), "관련된 문서가 없습니다.")
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
