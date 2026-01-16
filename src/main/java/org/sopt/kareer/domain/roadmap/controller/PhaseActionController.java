package org.sopt.kareer.domain.roadmap.controller;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.service.PhaseActionService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Phase Action API", description = "Phase Action 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phase-actions")
public class PhaseActionController {

    private final PhaseActionService phaseActionService;

    @PostMapping("/{phase-action-id}/todo")
    @Operation(summary = "Phase Action 기반 Todo 생성", description = "특정 Phase Action을 기반으로 Todo를 생성합니다.")
    @CustomExceptionDescription(CREATE_TODO)
    public ResponseEntity<BaseResponse<Void>> createPhaseActionTodo(@AuthenticationPrincipal Long memberId,
                                                                    @PathVariable("phase-action-id") Long phaseActionId) {
        phaseActionService.createPhaseActionTodo(memberId, phaseActionId);
        return ResponseEntity.ok(BaseResponse.ok(null, "Phase Action 기반 Todo가 생성되었습니다."));
    }
}
