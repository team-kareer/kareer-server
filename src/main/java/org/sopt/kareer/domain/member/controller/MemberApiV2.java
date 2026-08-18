package org.sopt.kareer.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member API V2", description = "회원 API 버전 2")
public interface MemberApiV2 {

    @PostMapping("/onboard")
    @Operation(summary = "회원 온보딩 V2", description = "PENDING 상태의 회원의 온보딩 결과를 저장합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_ONBOARD)
    ResponseEntity<BaseResponse<Void>> onboardMember(@AuthenticationPrincipal Long memberId,
                                                     @Valid @RequestBody MemberOnboardV2Request request);
}
