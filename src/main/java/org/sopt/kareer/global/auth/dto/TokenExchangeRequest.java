package org.sopt.kareer.global.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenExchangeRequest(
        @NotBlank(message = "code는 필수 값입니다.")
        String code
) {
}
