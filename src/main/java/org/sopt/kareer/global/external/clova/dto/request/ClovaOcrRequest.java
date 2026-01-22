package org.sopt.kareer.global.external.clova.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClovaOcrRequest(
        String version,
        String requestId,
        long timestamp,
        List<Image> images
) {
    public record Image(
            String format,
            String name,
            String data
    ) {}
}