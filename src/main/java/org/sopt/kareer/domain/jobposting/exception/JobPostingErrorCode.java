package org.sopt.kareer.domain.jobposting.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum JobPostingErrorCode implements ErrorCode {
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 채용 공고입니다."),
    RESUME_CONTEXT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이력서/자소서 기반 사용자 컨텍스트 생성에 실패했습니다."),
    TOO_MANY_FILES(HttpStatus.BAD_REQUEST.value(), "이력서/자소서는 최대 2개 업로드 가능합니다.")
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
