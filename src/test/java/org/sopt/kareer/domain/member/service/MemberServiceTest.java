package org.sopt.kareer.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.MemberStatusResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.entity.enums.RoadmapStatus;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberOnboardRequestFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberVisaRepository memberVisaRepository;

    @AfterEach
    void tearDown() {
        memberVisaRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("OAuth 신규 회원이면 저장 후 반환한다.")
    @Test
    void findOrCreateByOAuthCreatesMember() {
        OAuthAttributes attributes = createOAuthAttributes();

        Member member = memberService.findOrCreateByOAuth(attributes);

        assertThat(member.getId()).isNotNull();
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @DisplayName("이미 존재하는 OAuth 회원이면 새로 저장하지 않는다.")
    @Test
    void findOrCreateByOAuthReturnsExistingMember() {
        OAuthAttributes attributes = createOAuthAttributes();
        Member first = memberService.findOrCreateByOAuth(attributes);

        Member second = memberService.findOrCreateByOAuth(attributes);

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @DisplayName("온보딩 요청을 저장하면 회원 정보와 비자 정보가 갱신된다.")
    @Test
    void onboardMember() {
        Member member = memberRepository.save(Member.createOAuthMember(
                "test-user",
                OAuthProvider.GOOGLE,
                UUID.randomUUID().toString(),
                "test_image_url"
        ));
        MemberOnboardRequest request = MemberOnboardRequestFixture.create();

        memberService.onboardMember(request, member.getId());

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(updated.getName()).isEqualTo(request.name());
        assertThat(updated.getCountry()).isEqualTo(request.country());
        assertThat(updated.getPrimaryMajor()).isEqualTo(request.primaryMajor());
        assertThat(updated.getTargetJobSkill()).isEqualTo(request.targetJobSkill());
        assertThat(updated.getExpectedGraduationDate()).isEqualTo(request.expectedGraduationDate());

        List<MemberVisa> visas = memberVisaRepository.findAll();
        assertThat(visas).hasSize(1);
        MemberVisa savedVisa = visas.getFirst();
        assertThat(savedVisa.getVisaType()).isEqualTo(request.visaType());
        assertThat(savedVisa.getVisaPoint()).isEqualTo(request.visaPoint());
    }

    @DisplayName("회원 정보 조회 시 멤버와 비자 정보가 함께 반환된다.")
    @Test
    void getMemberInfo() {
        Member member = memberRepository.save(Member.builder()
                .name("tester")
                .status(MemberStatus.ACTIVE)
                .provider(OAuthProvider.GOOGLE)
                .providerId(UUID.randomUUID().toString())
                .country(Country.AUSTRALIA)
                .primaryMajor("Computer Science")
                .secondaryMajor("Mathematics")
                .targetJob("Backend Engineer")
                .graduationDate(LocalDate.of(2023, 2, 1))
                .languageLevel(LanguageLevel.LEVEL_3)
                .degree(Degree.OVERSEAS_BACHELORS)
                .targetJobSkill("Java")
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .build());
        MemberVisa activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD10(member));

        MemberInfoResponse response = memberService.getMemberInfo(member.getId());

        assertThat(response.memberId()).isEqualTo(member.getId());
        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.country()).isEqualTo(member.getCountry());
        assertThat(response.degree()).isEqualTo(member.getDegree().getDescription());
        assertThat(response.visaType()).isEqualTo(activeVisa.getVisaType());
    }

    @DisplayName("비자 정보가 없으면 회원 정보 조회 시 예외가 발생한다.")
    @Test
    void getMemberInfoWithoutVisa() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-10"));

        assertThatThrownBy(() -> memberService.getMemberInfo(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 상태 조회 시 비자 만료 정보가 포함된다.")
    @Test
    void getMemberStatus() {
        Member member = memberRepository.save(Member.builder()
                .name("tester")
                .status(MemberStatus.ACTIVE)
                .provider(OAuthProvider.GOOGLE)
                .providerId(UUID.randomUUID().toString())
                .country(Country.AUSTRALIA)
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .degree(Degree.OVERSEAS_BACHELORS)
                .languageLevel(LanguageLevel.LEVEL_5)
                .build());
        MemberVisa activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD2(member));

        MemberStatusResponse response = memberService.getMemberStatus(member.getId());

        assertThat(response.visaType()).isEqualTo(activeVisa.getVisaType());
        assertThat(response.visaExpiredAt()).isEqualTo(activeVisa.getVisaExpiredAt());
    }

    @DisplayName("상태 조회 시 ACTIVE 비자가 없으면 예외가 발생한다.")
    @Test
    void getMemberStatusWithoutVisa() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-11"));

        assertThatThrownBy(() -> memberService.getMemberStatus(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }

    private OAuthAttributes createOAuthAttributes() {
        String providerId = UUID.randomUUID().toString();
        return new OAuthAttributes(
                OAuthProvider.GOOGLE,
                providerId,
                providerId + "@example.com",
                "tester",
                "profile",
                true,
                Map.of()
        );
    }
}
