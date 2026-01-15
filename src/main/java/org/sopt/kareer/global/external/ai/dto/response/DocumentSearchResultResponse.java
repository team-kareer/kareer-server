package org.sopt.kareer.global.external.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record DocumentSearchResultResponse(

        @Schema(description = "문서 ID")
        UUID documentId,

        @Schema(description = "문서 청크 ID")
        UUID chunkId,

        @Schema(description = "청크 인덱스")
        Integer chunkIndex,

        @Schema(description = "문서 내용")
        String content,

        @Schema(description = "유사도 점수")
        double score
) {
}
