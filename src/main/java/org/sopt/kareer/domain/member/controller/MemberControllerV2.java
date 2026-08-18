package org.sopt.kareer.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.facade.MemberOnboardingFacade;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/members")
public class MemberControllerV2 implements MemberApiV2 {

    private final MemberOnboardingFacade memberOnboardingFacade;

    @Override
    public ResponseEntity<BaseResponse<Void>> onboardMember(@AuthenticationPrincipal Long memberId,
                                                            @Valid @RequestBody MemberOnboardV2Request request) {
        memberOnboardingFacade.onboard(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("회원 온보딩이 완료되었습니다."));
    }
}
