package org.sopt.kareer.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.sopt.kareer.domain.member.dto.request.MemberTermsRequest;
import org.sopt.kareer.domain.member.dto.request.MypageRequest;
import org.sopt.kareer.domain.member.dto.response.*;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.config.swagger.SwaggerResponseDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.*;

@Tag(name = "Member API")
public interface MemberApi {

    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.MEMBER_INFO)
    ResponseEntity<BaseResponse<MemberInfoResponse>> getMemberInfo(@AuthenticationPrincipal Long memberId);

    @GetMapping("/onboard/universities")
    @Operation(summary = "온보딩 대학교 목록 조회", description = "회원 온보딩 시 선택할 수 있는 대학교 목록을 조회합니다.")
    ResponseEntity<BaseResponse<OnboardUniversitiesResponse>> getOnboardUniversities();

    @GetMapping("/onboard/countries")
    @Operation(summary = "온보딩 국가 목록 조회", description = "회원 온보딩 시 선택할 수 있는 국가 목록을 조회합니다.")
    ResponseEntity<BaseResponse<OnboardCountriesResponse>> getOnboardCountries();

    @GetMapping("/onboard/majors")
    @Operation(summary = "온보딩 전공 목록 조회", description = "회원 온보딩 시 선택할 수 있는 전공 목록을 조회합니다.")
    ResponseEntity<BaseResponse<OnboardMajorsResponse>> getOnboardMajors();

    @GetMapping("/onboard/fields")
    @Operation(summary = "온보딩 관심 분야 목록 조회", description = "회원 온보딩 시 선택할 수 있는 관심 분야 목록을 조회합니다.")
    ResponseEntity<BaseResponse<OnboardFieldsResponse>> getOnboardFields();

    @GetMapping("me/status")
    @Operation(summary = "유저 상태 조회", description = "사용자의 비자 정보, 졸업 정보를 조회합니다.")
    @CustomExceptionDescription(USER_STATUS)
    ResponseEntity<BaseResponse<MemberStatusResponse>> getMemberStatus(@AuthenticationPrincipal Long memberId);

    @GetMapping("mypage")
    @Operation(summary = "마이페이지 조회", description = "마이페이지에서 유저 정보를 조회합니다.")
    @CustomExceptionDescription(GET_MYPAGE)
    ResponseEntity<BaseResponse<MypageResponse>> getMypage(@AuthenticationPrincipal Long memberId);

    @PutMapping("mypage")
    @Operation(summary = "마이페이지 수정", description = "마이페이지에서 유저 프로필을 수정합니다.")
    @CustomExceptionDescription(UPDATE_MYPAGE)
    ResponseEntity<BaseResponse<Void>> updateMypage(@AuthenticationPrincipal Long memberId,
                                                    @Valid @RequestBody MypageRequest request);

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @CustomExceptionDescription(MEMBER_DELETE)
    ResponseEntity<BaseResponse<Void>> deleteMember(@AuthenticationPrincipal Long memberId,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response);

    @PostMapping(value = "/onboard/ocr/visa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "온보딩 비자 OCR API", description = "온보딩 과정에서 유저의 비자 문서를 분석하여 정보를 추출합니다.")
    ResponseEntity<BaseResponse<OcrVisaResponse>> getVisaInfo(@RequestPart("file") MultipartFile file);

    @PostMapping(value = "/onboard/ocr/passport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "온보딩 여권 OCR API", description = "온보딩 과정에서 유저의 여권을 분석하여 정보를 추출합니다.")
    ResponseEntity<BaseResponse<OcrPassportResponse>> getPassportInfo(@RequestPart("file") MultipartFile file);

    @PostMapping("/term-agreements")
    @Operation(summary = "약관 동의", description = "약관에 동의합니다.")
    @CustomExceptionDescription(TERM_AGREE)
    ResponseEntity<BaseResponse<Void>> agreeTerms(@AuthenticationPrincipal Long memberId,
                                                  @RequestBody @Valid MemberTermsRequest request);

    @GetMapping("completion")
    @Operation(summary = "온보딩, 약관동의 여부 조회")
    @CustomExceptionDescription(GET_COMPLETION)
    ResponseEntity<BaseResponse<MemberCompletionResponse>> getMemberCompletionStatus(@AuthenticationPrincipal Long memberId);
}
