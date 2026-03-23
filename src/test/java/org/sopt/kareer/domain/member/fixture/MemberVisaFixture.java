package org.sopt.kareer.domain.member.fixture;

import java.time.LocalDate;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.sopt.kareer.domain.member.entity.enums.VisaType;

public class MemberVisaFixture {

    public static MemberVisa activeD2(Member member) {
        return MemberVisa.builder()
                .member(member)
                .visaType(VisaType.D2)
                .visaStatus(VisaStatus.ACTIVE)
                .visaExpiredAt(LocalDate.now().plusYears(1))
                .visaPoint(null)
                .visaStartDate(LocalDate.now().minusMonths(3))
                .build();
    }

    public static MemberVisa inactiveD2(Member member) {
        return MemberVisa.builder()
                .member(member)
                .visaType(VisaType.D2)
                .visaStatus(VisaStatus.INACTIVE)
                .visaExpiredAt(LocalDate.now().minusMonths(1))
                .visaPoint(null)
                .visaStartDate(LocalDate.now().minusYears(1))
                .build();
    }

    public static MemberVisa activeD10(Member member) {
        return MemberVisa.builder()
                .member(member)
                .visaType(VisaType.D10)
                .visaStatus(VisaStatus.ACTIVE)
                .visaExpiredAt(LocalDate.now().plusMonths(18))
                .visaPoint(70)
                .visaStartDate(LocalDate.now())
                .build();
    }
}
