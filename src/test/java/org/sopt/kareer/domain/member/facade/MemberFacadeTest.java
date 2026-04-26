package org.sopt.kareer.domain.member.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.MemberStatusResponse;
import org.sopt.kareer.domain.member.dto.response.MypageResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class MemberFacadeTest {

    @Autowired
    private MemberFacade memberFacade;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberVisaRepository memberVisaRepository;

    @AfterEach
    void tearDown() {
        memberVisaRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 정보 조회 시 멤버와 비자 정보가 함께 반환된다.")
    @Test
    void getMemberInfo() {
        Member member = memberRepository.save(Member.builder()
                .name("tester")
                .email("tester@example.com")
                .status(MemberStatus.ACTIVE)
                .provider(OAuthProvider.GOOGLE)
                .providerId(UUID.randomUUID().toString())
                .countryCode("australia")
                .primaryMajorCode("computer-science")
                .secondaryMajor("Mathematics")
                .targetJob("Backend Engineer")
                .graduationDate(LocalDate.of(2023, 2, 1))
                .languageLevel(LanguageLevel.LEVEL_3)
                .degreeCode("outside-korea-bachelor")
                .englishLevelCode("advanced")
                .universityCode("some-university")
                .targetJobSkill("Java")
                .build());
        var activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD10(member));

        MemberInfoResponse response = memberFacade.getMemberInfo(member.getId());

        assertThat(response.memberId()).isEqualTo(member.getId());
        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.visaType()).isEqualTo(activeVisa.getVisaType());
    }

    @DisplayName("비자 정보가 없으면 회원 정보 조회 시 예외가 발생한다.")
    @Test
    void getMemberInfoWithoutVisa() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-10"));

        assertThatThrownBy(() -> memberFacade.getMemberInfo(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 상태 조회 시 비자 만료 정보가 포함된다.")
    @Test
    void getMemberStatus() {
        Member member = memberRepository.save(Member.builder()
                .name("tester")
                .email("tester-status@example.com")
                .status(MemberStatus.ACTIVE)
                .provider(OAuthProvider.GOOGLE)
                .providerId(UUID.randomUUID().toString())
                .countryCode("australia")
                .degreeCode("outside-korea-bachelor")
                .languageLevel(LanguageLevel.LEVEL_5)
                .build());
        var activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD2(member));

        MemberStatusResponse response = memberFacade.getMemberStatus(member.getId());

        assertThat(response.visaType()).isEqualTo(activeVisa.getVisaType());
        assertThat(response.visaExpiredAt()).isEqualTo(activeVisa.getVisaExpiredAt());
    }

    @DisplayName("상태 조회 시 ACTIVE 비자가 없으면 예외가 발생한다.")
    @Test
    void getMemberStatusWithoutVisa() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-11"));

        assertThatThrownBy(() -> memberFacade.getMemberStatus(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }

    @DisplayName("마이페이지에서 유저 정보를 조회한다.")
    @Test
    void getMyPage() {
        Member member = memberRepository.save(MemberFixture.getMember());
        memberVisaRepository.save(MemberVisaFixture.activeD2(member));

        MypageResponse response = memberFacade.getMypage(member.getId());

        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.country()).isEqualTo(member.getCountryCode());
    }

    @DisplayName("존재하지 않는 회원의 마이페이지 조회 시 예외가 발생한다.")
    @Test
    void getMyPageWithoutMember() {
        assertThatThrownBy(() -> memberFacade.getMypage(1L))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("비자 정보가 존재하지 않는 경우 마이페이지 조회 시 예외가 발생한다.")
    @Test
    void getMypageWithoutMemberVisa() {
        Member member = memberRepository.save(MemberFixture.getMember());

        assertThatThrownBy(() -> memberFacade.getMypage(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }
}
