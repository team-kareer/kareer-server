package org.sopt.kareer.global.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequest(
        @NotBlank(message = "refreshToken은 필수 값입니다.")
        String refreshToken
) {
}
