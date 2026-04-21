package org.sopt.kareer.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberTermsRequest;
import org.sopt.kareer.domain.member.dto.request.MypageRequest;
import org.sopt.kareer.domain.member.dto.response.*;
import org.sopt.kareer.domain.member.facade.MemberFacade;
import org.sopt.kareer.domain.member.facade.MemberOcrFacade;
import org.sopt.kareer.domain.member.facade.MemberOnboardingFacade;
import org.sopt.kareer.domain.member.service.LocalizedOnboardQueryService;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {

    private final MemberFacade memberFacade;
    private final MemberOcrFacade memberOcrFacade;
    private final MemberOnboardingFacade memberOnboardingFacade;
    private final AuthService authService;
    private final LocalizedOnboardQueryService localizedOnboardQueryService;

    @Override
    public ResponseEntity<BaseResponse<MemberInfoResponse>> getMemberInfo(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberFacade.getMemberInfo(memberId), "회원 정보 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OnboardUniversitiesResponse>> getOnboardUniversities() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(localizedOnboardQueryService.getUniversities(), "온보딩 대학교 목록 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OnboardCountriesResponse>> getOnboardCountries() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(localizedOnboardQueryService.getCountries(), "온보딩 국가 목록 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OnboardMajorsResponse>> getOnboardMajors() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(localizedOnboardQueryService.getMajors(), "온보딩 전공 목록 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OnboardFieldsResponse>> getOnboardFields() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(localizedOnboardQueryService.getFields(), "온보딩 관심 분야 목록 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<MemberStatusResponse>> getMemberStatus(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberFacade.getMemberStatus(memberId), "My status 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<MypageResponse>> getMypage(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberFacade.getMypage(memberId), "마이페이지 조회에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> updateMypage(@AuthenticationPrincipal Long memberId,
                                                           @Valid @RequestBody MypageRequest request) {
        memberFacade.updateMypage(memberId, request.toCommand());
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("마이페이지 수정에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> deleteMember(@AuthenticationPrincipal Long memberId,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        memberFacade.deleteMember(memberId);
        authService.signOut(request, response);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("회원 탈퇴에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OcrVisaResponse>> getVisaInfo(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberOcrFacade.extractVisa(file), "사용자 비자 정보 추출에 성공했습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<OcrPassportResponse>> getPassportInfo(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberOcrFacade.extractPassport(file), "사용자 여권 정보 추출에 성공했습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> agreeTerms(@AuthenticationPrincipal Long memberId,
                                                         @RequestBody @Valid MemberTermsRequest request) {
        memberOnboardingFacade.agreeTerms(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("약관 동의 저장에 성공했습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<MemberCompletionResponse>> getMemberCompletionStatus(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(memberFacade.getCompletion(memberId), "온보딩/약관동의 여부 조회에 성공하였습니다."));
    }
}
