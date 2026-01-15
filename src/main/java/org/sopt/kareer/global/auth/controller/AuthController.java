package org.sopt.kareer.global.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.dto.TokenExchangeRequest;
import org.sopt.kareer.global.auth.dto.TokenExchangeResponse;
import org.sopt.kareer.global.auth.dto.TokenResponse;
import org.sopt.kareer.global.auth.service.AuthService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/code/exchange")
    public ResponseEntity<BaseResponse<TokenExchangeResponse>> exchange(@Valid @RequestBody TokenExchangeRequest request,
                                                                        HttpServletResponse response) {
        TokenExchangeResponse exchangeResponse = authService.exchange(request, response);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(exchangeResponse, "로그인 코드가 교환되었습니다."));
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenResponse>> reissue(HttpServletRequest request,
                                                               HttpServletResponse response) {
        TokenResponse tokenResponse = authService.reissue(request, response);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(tokenResponse, "토큰이 재발급되었습니다."));
    }
}
