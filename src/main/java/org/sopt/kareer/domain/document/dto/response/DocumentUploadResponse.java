package org.sopt.kareer.domain.document.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.ai.document.Document;

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
