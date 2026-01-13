package org.sopt.kareer.global.config.swagger;

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
    UPLOAD_PDF(new LinkedHashSet<>(Set.of(
            EXTRACT_TEXT_FAILED,
            EMBEDDING_FAILED
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