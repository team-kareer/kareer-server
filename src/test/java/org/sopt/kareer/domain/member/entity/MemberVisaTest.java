package org.sopt.kareer.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberVisaTest {

    @DisplayName("비자 정보를 수정한다.")
    @Test
    void updateVisa(){
       //given
        Member member = MemberFixture.getMember();
        MemberVisa memberVisa = MemberVisaFixture.activeD2(member);

        LocalDate newExpiredAt = LocalDate.of(2028, 1, 1);


        //when
        memberVisa.updateVisa(VisaType.D10, newExpiredAt);

       //then
        assertThat(memberVisa.getVisaType()).isEqualTo(VisaType.D10);
        assertThat(memberVisa.getVisaExpiredAt()).isEqualTo(newExpiredAt);
    }

}