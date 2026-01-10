package org.sopt.kareer.global.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.dto.TokenReissueRequest;
import org.sopt.kareer.global.auth.dto.TokenResponse;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        TokenResponse response = authService.reissue(request);
        return BaseResponse.ok(response, "토큰이 재발급되었습니다.");
    }
}
