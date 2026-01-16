package org.sopt.kareer.domain.jobposting.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class JobPostingException extends CustomException {

    public JobPostingException(JobPostingErrorCode errorCode) {
        super(errorCode);
    }
    public JobPostingException(JobPostingErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
