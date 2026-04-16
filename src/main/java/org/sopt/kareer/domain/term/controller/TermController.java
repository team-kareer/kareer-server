package org.sopt.kareer.domain.term.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.term.dto.response.TermsResponse;
import org.sopt.kareer.domain.term.service.TermService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermController implements TermApi {

    private final TermService termService;

    @Override
    public ResponseEntity<BaseResponse<TermsResponse>> getTerms() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(termService.getTerms(), "약관 조회에 성공했습니다."));
    }
}
