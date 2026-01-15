package org.sopt.kareer.global.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    LOGIN_CODE_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "로그인 코드가 만료되었거나 존재하지 않습니다."),
    LOGIN_CODE_ALREADY_USED(HttpStatus.CONFLICT.value(), "이미 사용된 로그인 코드입니다.");

    private final int httpStatus;
    private final String message;
}
