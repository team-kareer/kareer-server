package org.sopt.kareer.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_INFO)
    public ResponseEntity<BaseResponse<MemberInfoResponse>> getMemberInfo(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getMemberInfo(memberId), "회원 정보 조회에 성공하였습니다."));
    }

    @PostMapping("/onboard")
    @Operation(summary = "회원 온보딩", description = "PENDING 상태의 회원의 온보딩 결과를 저장합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_ONBOARD)
    public ResponseEntity<BaseResponse<Void>> onboardMember(@AuthenticationPrincipal Long memberId,
                                                            @Valid @RequestBody MemberOnboardRequest request) {
        memberService.onboardMember(request, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok("회원 온보딩이 완료되었습니다."));
    }
}
