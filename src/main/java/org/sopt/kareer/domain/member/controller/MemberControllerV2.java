package org.sopt.kareer.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.*;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/members")
@Tag(name = "Member API V2", description = "회원 API 버전 2")
public class MemberControllerV2 {

    private final MemberService memberService;

    @PostMapping("/onboard")
    @Operation(summary = "회원 온보딩 V2", description = "PENDING 상태의 회원의 온보딩 결과를 저장합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_ONBOARD)
    public ResponseEntity<BaseResponse<Void>> onboardMember(@AuthenticationPrincipal Long memberId,
                                                            @Valid @RequestBody MemberOnboardV2Request request) {
        memberService.onboardMemberV2(request, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok("회원 온보딩이 완료되었습니다."));
    }
}
