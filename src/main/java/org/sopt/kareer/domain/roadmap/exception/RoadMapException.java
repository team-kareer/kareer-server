package org.sopt.kareer.domain.roadmap.exception;

import org.sopt.kareer.global.exception.customexception.CustomException;

public class RoadMapException extends CustomException {

    public RoadMapException(RoadmapErrorCode errorCode) {
        super(errorCode);
    }

    public RoadMapException(RoadmapErrorCode errorCode ,String message) {
        super(errorCode, message);
    }
}
