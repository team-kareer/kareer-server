package org.sopt.kareer.global.external.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DocumentUploadResponse(
        @Schema(description = "문서 ID")
        String documentId
) {
    public static DocumentUploadResponse of(String documentId) {
        return DocumentUploadResponse.builder()
                .documentId(documentId)
                .build();
    }
}
