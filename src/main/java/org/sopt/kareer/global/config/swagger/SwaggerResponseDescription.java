package org.sopt.kareer.global.config.swagger;

import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.sopt.kareer.domain.member.exception.MemberErrorCode.*;
import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.*;
import static org.sopt.kareer.global.external.ai.exception.LlmErrorCode.LLM_JSON_PARSING_FAILED;
import static org.sopt.kareer.global.external.ai.exception.RagErrorCode.*;

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
    CREATE_ROADMAP(new LinkedHashSet<>(Set.of(
            LLM_JSON_PARSING_FAILED,
            DOCUMENTS_RETRIEVED_EMPTY,
            MEMBER_NOT_FOUND,
            PHASE_STATUS_INVALID,
            PHASE_STATUS_BLANK,
            PHASE_ACTION_TYPE_BLANK,
            PHASE_ACTION_TYPE_INVALID,
            ACTION_ITEM_TYPE_BLANK,
            ACTION_ITEM_TYPE_INVALID
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
