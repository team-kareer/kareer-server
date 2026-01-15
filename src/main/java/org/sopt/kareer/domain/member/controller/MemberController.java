package org.sopt.kareer.domain.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardCountriesResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardMajorsResponse;
import org.sopt.kareer.domain.member.entity.constants.Major;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.roadmap.service.RoadMapService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.CREATE_ROADMAP;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Member API")
public class MemberController {

    private final MemberService memberService;

    private final RoadMapService roadMapService;

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

    @GetMapping("/onboard/countries")
    @Operation(summary = "온보딩 국가 목록 조회", description = "회원 온보딩 시 선택할 수 있는 국가 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<OnboardCountriesResponse>> getOnboardCountries() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(
                        OnboardCountriesResponse.from(Country.getCountries()),
                        "온보딩 국가 목록 조회에 성공하였습니다."));
    }

    @GetMapping("/onboard/majors")
    @Operation(summary = "온보딩 전공 목록 조회", description = "회원 온보딩 시 선택할 수 있는 전공 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<OnboardMajorsResponse>> getOnboardMajors() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(OnboardMajorsResponse.from(Major.MAJOR_LIST), "온보딩 전공 목록 조회에 성공하였습니다.")
                );
    }

    @Operation(summary = "AI 로드맵 생성 API", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping("roadmap")
    public ResponseEntity<BaseResponse<Void>> generateRoadmap(
            @AuthenticationPrincipal Long memberId){

        roadMapService.createRoadmap(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("AI 로드맵 생성에 성공하였습니다."));
    }
}
