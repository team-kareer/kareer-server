package org.sopt.kareer.domain.roadmap.exception;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum RoadmapErrorCode implements ErrorCode {
    //Phase
    PHASE_STATUS_BLANK(HttpStatus.BAD_REQUEST.value(), "PhaseStatus가 비어있습니다."),
    PHASE_STATUS_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 PhaseStatus입니다."),
    PHASE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 Phase입니다."),

    //PhaseAction
    PHASE_ACTION_TYPE_BLANK(HttpStatus.BAD_REQUEST.value(), "PhaseActionType이 비어있습니다."),
    PHASE_ACTION_TYPE_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 PhaseActionType입니다."),
    PHASE_ACTION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "PhaseAction이 존재하지 않습니다."),

    //ActionItem
    ACTION_ITEM_TYPE_BLANK(HttpStatus.BAD_REQUEST.value(), "ActionItemType가 비어있습니다."),
    ACTION_ITEM_TYPE_INVALID(HttpStatus.BAD_REQUEST.value(), "잘못된 ActionItemType입니다."),

    // Etc..
    INVALID_DATE_TYPE(HttpStatus.BAD_REQUEST.value(), "올바른 날짜 형식이 아닙니다.")
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
