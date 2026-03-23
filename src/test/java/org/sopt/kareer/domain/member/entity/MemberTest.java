package org.sopt.kareer.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.EnglishLevel;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.fixture.MemberFixture;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
                Country.AFGHANISTAN,
                Degree.DOMESTIC_ASSOCIATE,
                "Konkuk University",
                "Computer Science",
                "Statistic",
                LanguageLevel.LEVEL_3,
                EnglishLevel.BEGINNER
        );

       //then
        assertThat(member.getTargetJob()).isEqualTo("Developer");
        assertThat(member.getBirthDate()).isEqualTo(newBirthDate);
        assertThat(member.getCountry()).isEqualTo(Country.AFGHANISTAN);
        assertThat(member.getDegree()).isEqualTo(Degree.DOMESTIC_ASSOCIATE);
        assertThat(member.getUniversity()).isEqualTo("Konkuk University");
        assertThat(member.getPrimaryMajor()).isEqualTo("Computer Science");
        assertThat(member.getSecondaryMajor()).isEqualTo("Statistic");
        assertThat(member.getLanguageLevel()).isEqualTo(LanguageLevel.LEVEL_3);
        assertThat(member.getEnglishLevel()).isEqualTo(EnglishLevel.BEGINNER);
    }
}