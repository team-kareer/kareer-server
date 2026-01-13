package org.sopt.kareer.global.config.swagger;

import static org.sopt.kareer.global.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.sopt.kareer.global.exception.errorcode.MemberErrorCode.ONBOARDING_ALREADY_COMPLETED;
import static org.sopt.kareer.global.exception.errorcode.MemberErrorCode.ONBOARDING_REQUIRED;

import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;

import java.util.LinkedHashSet;
import java.util.Set;

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
            ONBOARDING_ALREADY_COMPLETED
    ))),
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
