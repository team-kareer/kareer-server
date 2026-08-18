package org.sopt.kareer.domain.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.facade.ActionItemFacade;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap/action-items")
public class ActionItemController implements ActionItemApi {

    private final ActionItemFacade actionItemFacade;

    @Override
    public ResponseEntity<BaseResponse<Void>> toggleActionItemCompletion(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long actionItemId) {

        actionItemFacade.toggleCompletion(memberId, actionItemId);
        return ResponseEntity.ok(BaseResponse.ok("액션 아이템 완료 상태가 토글되었습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<ActionItemListResponse>> getAllActionItems(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.ok(
                BaseResponse.ok(actionItemFacade.getAllActionItems(memberId), "모든 액션 아이템이 조회되었습니다."));
    }
}
