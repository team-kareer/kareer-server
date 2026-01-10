package org.sopt.kareer.global.exception.errorcode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    MEMBER_INACTIVE(HttpStatus.BAD_REQUEST.value(), "비활성화된 회원입니다.");

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
