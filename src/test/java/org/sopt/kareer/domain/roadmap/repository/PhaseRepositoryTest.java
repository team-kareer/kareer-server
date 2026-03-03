package org.sopt.kareer.domain.roadmap.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
public class PhaseRepositoryTest {

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        phaseRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("phase 리스트를 조회한다.")
    class FindPhases {

        @Test
        @DisplayName("phase 리스트가 sequence 오름차순 정렬되어 정상적으로 조회된다.")
        void findPhases_success() {
            // given
            Member member = memberRepository.save((MemberFixture.getMember()));

            Phase phase3 = PhaseFixture.getPhase(member, 3, PhaseStatus.FUTURE);
            Phase phase1 = PhaseFixture.getPhase(member, 1, PhaseStatus.CURRENT);
            Phase phase2 = PhaseFixture.getPhase(member, 2, PhaseStatus.NEXT);
            phaseRepository.saveAll(List.of(phase1, phase2, phase3));

            // when
            List<PhaseResponse> response = phaseRepository.findPhases(member.getId());

            // then
            assertThat(response).hasSize(3);

            assertThat(response.get(0).sequence()).isEqualTo(1);
            assertThat(response.get(1).sequence()).isEqualTo(2);
            assertThat(response.get(2).sequence()).isEqualTo(3);
        }

        @Test
        @DisplayName("다른 회원의 Phase 리스트는 조회되지 않는다.")
        void findPhases_onlyOwnData() {
            // given
            Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));

            Phase phase1 = PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT);
            Phase phase2 = PhaseFixture.getPhase(member2, 1, PhaseStatus.CURRENT);

            phaseRepository.saveAll(List.of(phase1, phase2));

            // when
            List<PhaseResponse> response = phaseRepository.findPhases(member1.getId());

            // then
            assertThat(response).hasSize(1);
        }
    }
}
