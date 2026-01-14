package org.sopt.kareer.domain.member.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class MemberException extends CustomException {

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(MemberErrorCode errorCode, String messageDetail) {
        super(errorCode, messageDetail);
    }
}
