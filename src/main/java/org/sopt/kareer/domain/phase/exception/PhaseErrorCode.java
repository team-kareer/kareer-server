package org.sopt.kareer.domain.phase.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PhaseErrorCode implements ErrorCode {
    PHASE_STATUS_BLANK(HttpStatus.BAD_REQUEST.value(), "PhaseStatus가 비어있습니다."),
    PHASE_STATUS_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 PhaseStatus입니다."),
    PHASE_ACTION_TYPE_BLANK(HttpStatus.BAD_REQUEST.value(), "PhaseActionType이 비어있습니다."),
    PHASE_ACTION_TYPE_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 PhaseActionType입니다."),
    ACTION_ITEM_TYPE_BLANK(HttpStatus.BAD_REQUEST.value(), "ActionItemType가 비어있습니다."),
    ACTION_ITEM_TYPE_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 ActionItemType입니다."),
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
