package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.sopt.kareer.domain.member.util.VisaOcrParser;

import java.time.LocalDate;

@Builder
public record OcrVisaResponse(
        @Schema(description = "비자 유형")
        VisaType visaType,

        @Schema(description = "비자 발급일")
        LocalDate visaStartDate,

        @Schema(description = "비자 만료일")
        LocalDate visaExpiredAt
){
   public static OcrVisaResponse from(VisaOcrParser.VisaInfo visaInfo) {
        return OcrVisaResponse.builder()
                .visaType(visaInfo.visaType())
                .visaStartDate(visaInfo.visaStartDate())
                .visaExpiredAt(visaInfo.visaExpiredAt())
                .build();
   }
}

