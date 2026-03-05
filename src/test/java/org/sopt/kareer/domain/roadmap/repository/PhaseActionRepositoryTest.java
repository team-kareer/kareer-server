package org.sopt.kareer.domain.roadmap.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
public class PhaseActionRepositoryTest {

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    private Phase phase1;
    private Member member1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
        phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
    }

    @Test
    @DisplayName("phaseId가 일치하는 phaseAction들 중 아직 완료되지 않은 phaseAction들을 반환한다. ")
    void findByPhaseIdAndCompletedIsFalse() {
        // given
        Phase phase2 = phaseRepository.save(PhaseFixture.getPhase(member1, 2, PhaseStatus.NEXT));

        PhaseAction action1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
        PhaseAction action2 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
        action2.markCompleted();
        PhaseAction action3 = PhaseActionFixture.getPhaseAction(phase2, PhaseActionType.VISA);
        phaseActionRepository.saveAll(List.of(action1, action2, action3));

        // when
        List<PhaseAction> result = phaseActionRepository.findByPhaseIdAndCompletedIsFalse(phase1.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result)
                .extracting(PhaseAction::getId)
                .containsExactly(action1.getId());
        assertThat(result.get(0).getCompleted()).isFalse();
        assertThat(result.get(0).getPhase().getId()).isEqualTo(phase1.getId());
    }

    @Test
    @DisplayName("phaseActionId와 memberId가 일치하는 PhaseAction을 반환한다. ")
    void findByIdAndMemberId() {
        // given
        PhaseAction action1 = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

        // when
        Optional<PhaseAction> result = phaseActionRepository.findByIdAndMemberId(action1.getId(), member1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(action1.getId());
        assertThat(result.get().getPhase().getMember().getId())
                .isEqualTo(member1.getId());
    }
}
