package org.sopt.kareer.domain.roadmap.controller;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.service.PhaseActionService;
import org.sopt.kareer.domain.roadmap.service.PhaseService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Phase Action API", description = "Phase Action 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phase-actions")
public class PhaseActionController {

    private final PhaseActionService phaseActionService;
    private final PhaseService phaseService;

    @PostMapping("/{phase-action-id}/todo")
    @Operation(summary = "Phase Action 기반 Todo 생성", description = "특정 Phase Action을 기반으로 Todo를 생성합니다.")
    @CustomExceptionDescription(CREATE_TODO)
    public ResponseEntity<BaseResponse<Void>> createPhaseActionTodo(@AuthenticationPrincipal Long memberId,
                                                                    @PathVariable("phase-action-id") Long phaseActionId) {
        phaseActionService.createPhaseActionTodo(memberId, phaseActionId);
        return ResponseEntity.ok(BaseResponse.ok("Phase Action 기반 Todo가 생성되었습니다."));
    }

    @GetMapping("/{phaseActionId}/guide")
    @Operation(summary = "AI 가이드 조회", description = "AI 가이드를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.AI_GUIDE)
    public ResponseEntity<BaseResponse<AiGuideResponse>> getAiGuide(
            @PathVariable Long phaseActionId
    ) {
        AiGuideResponse response = phaseService.getAiGuide(phaseActionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(response, "AI 가이드가 조회되었습니다.")
                );
    }
}
