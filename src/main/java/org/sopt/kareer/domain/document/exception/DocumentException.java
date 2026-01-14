package org.sopt.kareer.domain.document.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.exception.errorcode.ErrorCode;

public class DocumentException extends CustomException {
    public DocumentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DocumentException(ErrorCode errorCode){
      super(errorCode);
    }
}
