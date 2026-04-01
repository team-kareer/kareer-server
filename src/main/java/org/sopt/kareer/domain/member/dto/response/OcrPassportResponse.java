package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.util.PassportOcrParser;

import java.time.LocalDate;

@Builder
public record OcrPassportResponse(
        @Schema(description = "Full Name", example = "Hong Seungwon")
        String fullName,

        @Schema(description = "국가", example = "AFGHANISTAN")
        Country country,

        @Schema(description = "생년월일")
        LocalDate birthDate
) {
   public static OcrPassportResponse from(PassportOcrParser.PassportInfo passportInfo) {
           return OcrPassportResponse.builder()
                   .fullName(passportInfo.fullName())
                   .country(passportInfo.country())
                   .birthDate(passportInfo.birthDate())
                   .build();
   }
}
