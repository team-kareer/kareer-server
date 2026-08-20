package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.kareer.domain.roadmap.dto.request.ActionItemCreateRequest;
import org.sopt.kareer.domain.roadmap.dto.request.ActionItemUpdateRequest;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemResponse;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Action Item API", description = "Action Item 관련 API")
public interface ActionItemApi {

    @Operation(summary = "사용자 액션 아이템 생성", description = "사용자가 직접 액션 아이템을 생성합니다.")
    @PostMapping
    ResponseEntity<BaseResponse<ActionItemResponse>> createActionItem(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody ActionItemCreateRequest request
    );

    @Operation(summary = "액션 아이템 수정", description = "액션 아이템의 제목 또는 마감일을 수정합니다.")
    @PatchMapping("/{actionItemId}")
    ResponseEntity<BaseResponse<ActionItemResponse>> updateActionItem(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long actionItemId,
            @Valid @RequestBody ActionItemUpdateRequest request
    );

    @Operation(summary = "액션 아이템 삭제", description = "액션 아이템을 삭제합니다.")
    @DeleteMapping("/{actionItemId}")
    ResponseEntity<Void> deleteActionItem(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long actionItemId
    );

    @Operation(summary = "액션 아이템 완료 상태 토글", description = "특정 액션 아이템의 완료 상태를 토글합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.TOGGLE_ACTION_ITEM_COMPLETION)
    @PatchMapping("/{actionItemId}/completed")
    ResponseEntity<BaseResponse<Void>> toggleActionItemCompletion(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long actionItemId);

    @Operation(summary = "액션 아이템 전체 조회", description = "로그인한 회원의 활성화된 모든 액션 아이템을 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.GET_ALL_ACTION_ITEMS)
    @GetMapping
    ResponseEntity<BaseResponse<ActionItemListResponse>> getAllActionItems(@AuthenticationPrincipal Long memberId);
}
