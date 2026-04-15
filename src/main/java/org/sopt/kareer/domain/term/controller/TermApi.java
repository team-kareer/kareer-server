package org.sopt.kareer.domain.term.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.kareer.domain.term.dto.response.TermsResponse;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Term API")
public interface TermApi {

    @GetMapping
    @Operation(summary = "약관 조회", description = "약관 내용을 조회합니다.")
    ResponseEntity<BaseResponse<TermsResponse>> getTerms();
}
