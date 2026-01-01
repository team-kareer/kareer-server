package org.sopt.kareer.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;

@Getter
@AllArgsConstructor
public class BaseErrorResponse {
    private final int code;
    private final String message;
    private final String messageDetail;

    public static BaseErrorResponse of(int code, String message, String messageDetail) {
        return new BaseErrorResponse(code, message, messageDetail);
    }

    public static BaseErrorResponse of(ErrorCode errorCode) {
        return new BaseErrorResponse(errorCode.getHttpStatus(),errorCode.getMessage(), null);
    }

    public static BaseErrorResponse of(ErrorCode errorCode, String messageDetail){
        return new BaseErrorResponse(errorCode.getHttpStatus(),errorCode.getMessage(), messageDetail);
    }

}
