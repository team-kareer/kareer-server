package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.service.ActionItemService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Action Item API", description = "Action Item 관련 API")
@RestController
@RequestMapping("/api/v1/action-items")
@RequiredArgsConstructor
public class ActionItemController {

    private final ActionItemService actionItemService;

    @PatchMapping("/{actionItemId}/completed")
    @Operation(summary = "액션 아이템 완료 상태 토글", description = "특정 액션 아이템의 완료 상태를 토글합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.TOGGLE_ACTION_ITEM_COMPLETION)
    public ResponseEntity<BaseResponse<Void>> toggleActionItemCompletion(@AuthenticationPrincipal Long memberId,
                                                                         @PathVariable Long actionItemId) {
        actionItemService.toggleCompletion(memberId, actionItemId);
        return ResponseEntity.ok(BaseResponse.ok("액션 아이템 완료 상태가 토글되었습니다."));
    }

    @GetMapping("")
    @Operation(summary = "액션 아이템 전체 조회", description = "로그인한 회원의 활성화된 모든 액션 아이템을 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.GET_ALL_ACTION_ITEMS)
    public ResponseEntity<BaseResponse<ActionItemListResponse>> getAllActionItems(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(
                BaseResponse.ok(actionItemService.getAllActionItems(memberId), "모든 액션 아이템이 조회되었습니다.")
        );
    }
}
