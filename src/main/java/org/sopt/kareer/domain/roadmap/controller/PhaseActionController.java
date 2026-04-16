package org.sopt.kareer.domain.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.service.PhaseActionService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap/phase-actions")
public class PhaseActionController implements PhaseActionApi {

    private final PhaseActionService phaseActionService;

    @Override
    public ResponseEntity<BaseResponse<Void>> createPhaseActionTodo(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseActionId) {

        phaseActionService.createPhaseActionTodo(memberId, phaseActionId);
        return ResponseEntity.ok(BaseResponse.ok("Phase Action 기반 Todo가 생성되었습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<AiGuideResponse>> getAiGuide(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long phaseActionId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(phaseActionService.getAiGuide(memberId, phaseActionId), "AI 가이드가 조회되었습니다."));
    }
}
