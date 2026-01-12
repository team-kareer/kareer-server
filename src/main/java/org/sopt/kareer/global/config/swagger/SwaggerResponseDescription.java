package org.sopt.kareer.global.config.swagger;

import lombok.Getter;
import org.sopt.kareer.domain.exception.DocumentErrorCode;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.sopt.kareer.domain.exception.DocumentErrorCode.*;

@Getter
public enum SwaggerResponseDescription {
    DEFAULT(new LinkedHashSet<>(Set.of(
    ))),
    UPLOAD_PDF(new LinkedHashSet<>(Set.of(
            DOCUMENT_PROCESSING_FAILED,
            EXTRACT_TEXT_FAILED
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