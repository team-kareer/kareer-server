package org.sopt.kareer.domain.term.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class TermException extends CustomException {
    public TermException(TermErrorCode errorCode) {
        super(errorCode);
    }
}
