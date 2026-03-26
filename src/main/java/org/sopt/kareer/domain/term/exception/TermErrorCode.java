package org.sopt.kareer.domain.term.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum TermErrorCode implements ErrorCode {

    TERM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 약관입니다."),
    DUPLICATE_TERM(HttpStatus.BAD_REQUEST.value(), "중복된 약관 ID가 존재합니다."),
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST.value(), "필수 약관에 동의해야 합니다."),
    MISSING_TERM(HttpStatus.BAD_REQUEST.value(), "누락된 약관 동의가 존재합니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST.value(), "잘못된 요청 파라미터입니다.")
    ;

    private final int httpStatus;
    private final String message;
}
