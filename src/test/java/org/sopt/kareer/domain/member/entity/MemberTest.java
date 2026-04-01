package org.sopt.kareer.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.*;
import org.sopt.kareer.domain.member.entity.enums.*;
import org.sopt.kareer.domain.member.fixture.MemberFixture;

class MemberTest {

    @DisplayName("프로필 정보를 수정한다.")
    @Test
    void updateProfile(){
       //given
        Member member = MemberFixture.getMember(LocalDate.of(2000,1,1));
        LocalDate newBirthDate = LocalDate.of(2000,11,12);

       //when
        member.updateProfile(
                "Developer",
                newBirthDate,
                "afghanistan",
                Degree.DOMESTIC_ASSOCIATE,
                "konkuk-university",
                "computer-science",
                "Statistic",
                LanguageLevel.LEVEL_3,
                EnglishLevel.BEGINNER
        );

       //then
        assertThat(member.getTargetJob()).isEqualTo("Developer");
        assertThat(member.getBirthDate()).isEqualTo(newBirthDate);
        assertThat(member.getCountryCode()).isEqualTo("afghanistan");
        assertThat(member.getDegreeCode()).isEqualTo("south-korea-associate");
        assertThat(member.getUniversityCode()).isEqualTo("konkuk-university");
        assertThat(member.getPrimaryMajorCode()).isEqualTo("computer-science");
        assertThat(member.getSecondaryMajor()).isEqualTo("Statistic");
        assertThat(member.getLanguageLevel()).isEqualTo(LanguageLevel.LEVEL_3);
        assertThat(member.getUniversityCode()).isEqualTo("beginner");
    }
}
