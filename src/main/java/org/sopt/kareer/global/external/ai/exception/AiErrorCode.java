package org.sopt.kareer.global.external.ai.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {
    EXTRACT_TEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 파일에서 텍스트 추출에 실패하였습니다."),
    EMBEDDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PDF 업로드 및 임베딩에 실패했습니다."),
    LLM_JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LLM 응답 JSON 파싱에 실패했습니다."),
    DOCUMENTS_RETRIEVED_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "관련된 문서가 없어 로드맵 생성에 실패했습니다."),
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
