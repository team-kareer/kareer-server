package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.CREATE_TODO;

@Tag(name = "Phase Action API", description = "Phase Action 관련 API")
public interface PhaseActionApi {

    @Operation(summary = "Phase Action 기반 Todo 생성", description = "특정 Phase Action을 기반으로 Todo를 생성합니다.")
    @CustomExceptionDescription(CREATE_TODO)
    @PostMapping("/{phaseActionId}/todo")
    ResponseEntity<BaseResponse<Void>> createPhaseActionTodo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseActionId);

    @Operation(summary = "AI 가이드 조회", description = "AI 가이드를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.AI_GUIDE)
    @GetMapping("/{phaseActionId}/guide")
    ResponseEntity<BaseResponse<AiGuideResponse>> getAiGuide(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseActionId);
}
