package org.sopt.kareer.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaType;

import java.time.LocalDate;

@Builder
public record MemberStatusResponse(
        @Schema(description = "비자 상태")
        VisaType visaType,

        @Schema(description = "비자 만료일")
        LocalDate visaExpiredAt,

        @Schema(description = "졸업 예정일")
        LocalDate expectedGraduationDate,

        @Schema(description = "졸업일")
        LocalDate graduationDate
) {
    public static MemberStatusResponse from(Member member, MemberVisa visa) {
        return MemberStatusResponse.builder()
                .visaType(visa.getVisaType())
                .visaExpiredAt(visa.getVisaExpiredAt())
                .graduationDate(member.getGraduationDate())
                .expectedGraduationDate(member.getExpectedGraduationDate())
                .build();
    }
}
