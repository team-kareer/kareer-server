package org.sopt.kareer.domain.jobposting.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum JobPostingErrorCode implements ErrorCode {
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 채용 공고입니다.")
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
