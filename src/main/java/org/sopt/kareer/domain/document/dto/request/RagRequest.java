package org.sopt.kareer.domain.document.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RagRequest(
        @Schema(description = "질문")
        @NotBlank
        String query
) {
}
