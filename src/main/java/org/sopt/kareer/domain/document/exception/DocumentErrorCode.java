package org.sopt.kareer.domain.document.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DocumentErrorCode implements ErrorCode {
    EXTRACT_TEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 파일에서 텍스트 추출에 실패하였습니다."),
    EMBEDDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 업로드 및 임베딩에 실패했습니다.")
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
