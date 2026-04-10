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

        @Schema(description = "국가 코드 및 라벨", example = "{\"code\": \"south-korea\", \"label\": \"South Korea\"}")
        LocalizedItemResponse country,

        @Schema(description = "생년월일")
        LocalDate birthDate
) {
   public static OcrPassportResponse from(PassportOcrParser.PassportInfo passportInfo) {
           Country country = passportInfo.country();
           return OcrPassportResponse.builder()
                   .fullName(passportInfo.fullName())
                   .country(country != null ? new LocalizedItemResponse(country.getCode(), country.getCountryName()) : null)
                   .birthDate(passportInfo.birthDate())
                   .build();
   }
}
