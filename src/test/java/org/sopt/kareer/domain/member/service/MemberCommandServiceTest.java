package org.sopt.kareer.domain.member.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.fixture.MemberOnboardRequestFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MemberCommandServiceTest {

    @Autowired
    private MemberCommandService memberCommandService;

    @Autowired
    private MemberQueryService memberQueryService;

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

        Member member = memberCommandService.findOrCreateByOAuth(attributes);

        assertThat(member.getId()).isNotNull();
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @DisplayName("이미 존재하는 OAuth 회원이면 새로 저장하지 않는다.")
    @Test
    void findOrCreateByOAuthReturnsExistingMember() {
        OAuthAttributes attributes = createOAuthAttributes();
        Member first = memberCommandService.findOrCreateByOAuth(attributes);

        Member second = memberCommandService.findOrCreateByOAuth(attributes);

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @DisplayName("온보딩 요청을 저장하면 회원 정보와 비자 정보가 갱신된다.")
    @Test
    void onboard() {
        Member member = memberRepository.save(Member.createOAuthMember(
                "test-user",
                OAuthProvider.GOOGLE,
                UUID.randomUUID().toString(),
                "test_image_url",
                "test-user@example.com"
        ));
        MemberOnboardV2Request request = MemberOnboardRequestFixture.create();

        memberCommandService.onboard(member, request);

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(updated.getName()).isEqualTo(request.name());
        assertThat(updated.getCountryCode()).isEqualTo(request.countryCode());
        assertThat(updated.getPrimaryMajorCode()).isEqualTo(request.primaryMajorCode());
        assertThat(updated.getTargetJobSkill()).isEqualTo(request.targetJobSkill());
        assertThat(updated.getExpectedGraduationDate()).isEqualTo(request.expectedGraduationDate());

        List<MemberVisa> visas = memberVisaRepository.findAll();
        assertThat(visas).hasSize(1);
        assertThat(visas.getFirst().getVisaType()).isEqualTo(request.visaType());
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
