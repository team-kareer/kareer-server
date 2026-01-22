package org.sopt.kareer.global.external.clova.dto.response;

import java.util.List;

public record ClovaOcrResponse(
        String version,
        String requestId,
        long timestamp,
        List<ImageResult> images
) {
    public record ImageResult(
            List<Field> fields
    ) {}

    public record Field(
            String inferText
    ) {}
}
