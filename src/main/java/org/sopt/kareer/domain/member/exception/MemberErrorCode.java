package org.sopt.kareer.domain.member.exception;

import lombok.AllArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    MEMBER_INACTIVE(HttpStatus.BAD_REQUEST.value(), "비활성화된 회원입니다."),
    ONBOARDING_REQUIRED(HttpStatus.BAD_REQUEST.value(), "온보딩이 완료되지 않은 회원입니다."),
    ONBOARDING_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST.value(), "이미 온보딩이 완료된 회원입니다."),
    INVALID_VISA_POINT(HttpStatus.BAD_REQUEST.value(), "visaPoint는 D10 비자인 경우에만 입력할 수 있습니다."),
    INVALID_COUNTRY(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 국가입니다.")
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
