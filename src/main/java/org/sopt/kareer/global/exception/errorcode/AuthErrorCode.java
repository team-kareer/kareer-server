package org.sopt.kareer.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 OAuth 제공자입니다."),
    JWT_MEMBER_ID_EXTRACTION_FAILED(HttpStatus.UNAUTHORIZED.value(), "JWT에서 memberId 추출 실패"),
    JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "잘못된 JWT 서명입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED.value(), "지원하지 않는 JWT입니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰 정보가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),
    AUTHENTICATION_SETTING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증 정보 처리에 실패했습니다");

    private final int httpStatus;
    private final String message;
}
