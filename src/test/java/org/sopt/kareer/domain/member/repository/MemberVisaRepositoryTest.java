package org.sopt.kareer.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.fixture.MemberVisaFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class MemberVisaRepositoryTest {

    @Autowired
    private MemberVisaRepository memberVisaRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("ACTIVE 상태의 비자만 조회된다.")
    @Test
    void findActiveByMemberId() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-4"));
        MemberVisa activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD2(member));

        Optional<MemberVisa> found = memberVisaRepository.findActiveByMemberId(member.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(activeVisa.getId());
    }

    @DisplayName("ACTIVE 상태 비자가 없으면 Optional.empty를 반환한다.")
    @Test
    void findActiveByMemberIdWithoutActiveVisa() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-5"));
        memberVisaRepository.save(MemberVisaFixture.inactiveD2(member));

        Optional<MemberVisa> found = memberVisaRepository.findActiveByMemberId(member.getId());

        assertThat(found).isEmpty();
    }

    @DisplayName("특정 상태에 해당하는 비자 목록만 필터링된다.")
    @Test
    void findAllByMemberIdAndVisaStatus() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-6"));
        MemberVisa activeVisa = memberVisaRepository.save(MemberVisaFixture.activeD2(member));
        memberVisaRepository.save(MemberVisaFixture.inactiveD2(member));

        List<MemberVisa> visas = memberVisaRepository.findAllByMemberIdAndVisaStatus(
                member.getId(),
                VisaStatus.ACTIVE
        );

        assertThat(visas)
                .hasSize(1)
                .first()
                .extracting(MemberVisa::getId)
                .isEqualTo(activeVisa.getId());
    }
}
