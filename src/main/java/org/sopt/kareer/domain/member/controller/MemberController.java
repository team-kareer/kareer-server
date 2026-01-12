package org.sopt.kareer.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<MemberInfoResponse>> getMemberInfo(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getMemberInfo(memberId), "회원 정보 조회에 성공하였습니다."));
    }
}
