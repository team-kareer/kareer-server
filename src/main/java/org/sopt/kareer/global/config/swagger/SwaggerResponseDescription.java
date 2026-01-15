package org.sopt.kareer.global.config.swagger;

import static org.sopt.kareer.domain.member.exception.MemberErrorCode.INVALID_COUNTRY;
import static org.sopt.kareer.domain.member.exception.MemberErrorCode.INVALID_VISA_POINT;
import static org.sopt.kareer.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.sopt.kareer.domain.member.exception.MemberErrorCode.ONBOARDING_ALREADY_COMPLETED;
import static org.sopt.kareer.domain.member.exception.MemberErrorCode.ONBOARDING_REQUIRED;
import static org.sopt.kareer.global.auth.exception.AuthErrorCode.LOGIN_CODE_ALREADY_USED;
import static org.sopt.kareer.global.auth.exception.AuthErrorCode.LOGIN_CODE_NOT_FOUND;

import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.sopt.kareer.domain.document.exception.DocumentErrorCode.EMBEDDING_FAILED;
import static org.sopt.kareer.domain.document.exception.DocumentErrorCode.EXTRACT_TEXT_FAILED;

@Getter
public enum SwaggerResponseDescription {
    DEFAULT(new LinkedHashSet<>(Set.of(
    ))),
    MEMBER_INFO(new LinkedHashSet<>(Set.of(
            MEMBER_NOT_FOUND,
            ONBOARDING_REQUIRED
    ))),
    MEMBER_ONBOARD(new LinkedHashSet<>(Set.of(
            MEMBER_NOT_FOUND,
            ONBOARDING_ALREADY_COMPLETED,
            INVALID_COUNTRY,
            INVALID_VISA_POINT
    ))),
    UPLOAD_PDF(new LinkedHashSet<>(Set.of(
            EXTRACT_TEXT_FAILED,
            EMBEDDING_FAILED
    ))),
    AUTH_LOGIN_CODE(new LinkedHashSet<>(Set.of(
            LOGIN_CODE_NOT_FOUND,
            LOGIN_CODE_ALREADY_USED
    ))),
    AUTH_REISSUE(new LinkedHashSet<>(Set.of(
    )))

    ;
    private final Set<ErrorCode> errorCodeList;
    SwaggerResponseDescription(Set<ErrorCode> specificErrorCodes) {
        this.errorCodeList = new LinkedHashSet<>();
        this.errorCodeList.addAll(specificErrorCodes);
        this.errorCodeList.addAll(getGlobalErrorCodes());
    }

    private Set<ErrorCode> getGlobalErrorCodes() {
        return new LinkedHashSet<>(Set.of(GlobalErrorCode.values()));
    }
}
