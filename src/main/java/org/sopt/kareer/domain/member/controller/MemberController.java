package org.sopt.kareer.domain.member.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.request.MypageRequest;
import org.sopt.kareer.domain.member.dto.response.*;
import org.sopt.kareer.domain.member.entity.constants.Field;
import org.sopt.kareer.domain.member.entity.constants.Major;
import org.sopt.kareer.domain.member.entity.constants.University;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.domain.roadmap.service.RoadMapService;
import org.sopt.kareer.domain.roadmap.service.RoadmapAsyncService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Member API")
public class MemberController {

    private final MemberService memberService;
    private final RoadMapService roadMapService;
    private final RoadmapAsyncService roadmapAsyncService;
    private final AuthService authService;

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

    @GetMapping("/onboard/universities")
    @Operation(summary = "온보딩 대학교 목록 조회", description = "회원 온보딩 시 선택할 수 있는 대학교 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<OnboardUniversitiesResponse>> getOnboardUniversities() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(OnboardUniversitiesResponse.from(University.UNIVERSITY_LIST),
                        "온보딩 대학교 목록 조회에 성공하였습니다."));
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

    @GetMapping("/onboard/fields")
    @Operation(summary = "온보딩 관심 분야 목록 조회", description = "회원 온보딩 시 선택할 수 있는 관심 분야 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<OnboardFieldsResponse>> getOnboardFields() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.ok(OnboardFieldsResponse.from(Field.FIELD_LIST), "온보딩 관심 분야 목록 조회에 성공하였습니다.")
                );
    }

    @Operation(summary = "AI 로드맵 생성 API", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping("roadmap")
    public ResponseEntity<BaseResponse<Void>> generateRoadmap(
            @AuthenticationPrincipal Long memberId) {

        roadMapService.createRoadmap(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("AI 로드맵 생성에 성공하였습니다."));
    }

    @Operation(summary = "AI 로드맵 생성 테스트용 API (Server Only)", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping("roadmap/test")
    public ResponseEntity<BaseResponse<RoadmapTestResponse>> generateRoadmapForTest(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(roadMapService.createRoadmapTest(memberId), "AI 로드맵 생성에 성공하였습니다."));
    }

    @Operation(summary = "유저 상태 조회", description = "사용자의 비자 정보, 졸업 정보를 조회합니다.")
    @CustomExceptionDescription(USER_STATUS)
    @GetMapping("me/status")
    public ResponseEntity<BaseResponse<MemberStatusResponse>> getMemberStatus(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getMemberStatus(memberId), "My status 조회에 성공하였습니다."));
    }

    @Operation(summary = "마이페이지 조회", description = "마이페이지에서 유저 정보를 조회합니다.")
    @CustomExceptionDescription(GET_MYPAGE)
    @GetMapping("mypage")
    public ResponseEntity<BaseResponse<MypageResponse>> getMypage(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getMypage(memberId), "마이페이지 조회에 성공하였습니다."));
    }

    @Operation(summary = "마이페이지 수정", description = "마이페이지에서 유저 프로필을 수정합니다.")
    @CustomExceptionDescription(UPDATE_MYPAGE)
    @PutMapping("mypage")
    public ResponseEntity<BaseResponse<Void>> updateMypage(@AuthenticationPrincipal Long memberId,
                                                           @Valid @RequestBody MypageRequest request) {
        memberService.updateMypage(memberId, request.toCommand());
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("마이페이지 수정에 성공하였습니다."));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @CustomExceptionDescription(MEMBER_DELETE)
    @DeleteMapping("/me")
    public ResponseEntity<BaseResponse<Void>> deleteMember(@AuthenticationPrincipal Long memberId,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        memberService.deleteMember(memberId);
        authService.signOut(request, response);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("회원 탈퇴에 성공하였습니다."));
    }

    @Operation(summary = "온보딩 비자 OCR API", description = "온보딩 과정에서 유저의 비자 문서를 분석하여 정보를 추출합니다.")
    @PostMapping(value = "/onboard/ocr/visa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<OcrVisaResponse>> getVisaInfo(
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getVisaOcr(file), "사용자 비자 정보 추출에 성공했습니다."));
    }

    @Operation(summary = "온보딩 여권 OCR API", description = "온보딩 과정에서 유저의 여권을 분석하여 정보를 추출합니다.")
    @PostMapping(value = "/onboard/ocr/passport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<OcrPassportResponse>> getPassportInfo(
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberService.getPassportOcr(file), "사용자 여권 정보 추출에 성공했습니다."));
    }


}
