package org.sopt.kareer.domain.member.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.member.service.dto.response.MemberCompletion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class MemberQueryServiceTest {

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

    @DisplayName("존재하는 회원 ID로 조회하면 회원을 반환한다.")
    @Test
    void getMemberById() {
        Member saved = memberRepository.save(MemberFixture.getMember());

        Member found = memberQueryService.getMemberById(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @DisplayName("존재하지 않는 회원 ID로 조회하면 예외가 발생한다.")
    @Test
    void getMemberByIdNotFound() {
        assertThatThrownBy(() -> memberQueryService.getMemberById(999L))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("ACTIVE 비자가 있는 회원의 비자를 조회한다.")
    @Test
    void getVisaByMemberId() {
        Member member = memberRepository.save(MemberFixture.getMember());
        MemberVisa saved = memberVisaRepository.save(MemberVisaFixture.activeD2(member));

        MemberVisa found = memberQueryService.getVisaByMemberId(member.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getVisaType()).isEqualTo(saved.getVisaType());
    }

    @DisplayName("ACTIVE 비자가 없으면 예외가 발생한다.")
    @Test
    void getVisaByMemberIdNotFound() {
        Member member = memberRepository.save(MemberFixture.getMember());

        assertThatThrownBy(() -> memberQueryService.getVisaByMemberId(member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.VISA_NOT_FOUND.getMessage());
    }

    @DisplayName("PENDING 상태 회원은 온보딩이 필요하다.")
    @Test
    void getMemberCompletionPendingMember() {
        Member member = memberRepository.save(MemberFixture.getMember("pending-provider"));
        member = memberRepository.save(Member.builder()
                .name(member.getName())
                .email(member.getEmail())
                .provider(member.getProvider())
                .providerId("pending-provider-2")
                .status(MemberStatus.PENDING)
                .build());

        MemberCompletion completion = memberQueryService.getMemberCompletion(member.getId());

        assertThat(completion.onboardingRequired()).isTrue();
        assertThat(completion.agreeTerm()).isFalse();
    }

    @DisplayName("ACTIVE 상태이고 약관 동의를 하지 않은 회원의 완료 상태를 조회한다.")
    @Test
    void getMemberCompletionActiveMemberWithoutTerms() {
        Member member = memberRepository.save(MemberFixture.getMember());

        MemberCompletion completion = memberQueryService.getMemberCompletion(member.getId());

        assertThat(completion.onboardingRequired()).isFalse();
        assertThat(completion.agreeTerm()).isFalse();
    }
}
