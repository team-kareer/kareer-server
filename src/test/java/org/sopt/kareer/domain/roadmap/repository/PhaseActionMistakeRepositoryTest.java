package org.sopt.kareer.domain.roadmap.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionMistake;
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

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
public class PhaseActionMistakeRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    @Autowired
    private PhaseActionMistakeRepository phaseActionMistakeRepository;

    @Test
    @DisplayName("특정 phaseActionId에 대한 PhaseActionMistake content를 id 오름차순으로 반환한다.")
    void findContentByPhaseActionId() {
        // given
        Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
        Phase phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));

        PhaseAction action1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
        PhaseAction action2 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
        phaseActionRepository.saveAll(List.of(action1, action2));

        PhaseActionMistake mistake1 = PhaseActionMistake.create("test-mistake-content-1", action1);
        PhaseActionMistake mistake2 = PhaseActionMistake.create("test-mistake-content-2", action1);
        PhaseActionMistake mistake3 = PhaseActionMistake.create("test-mistake-content-3", action2);
        phaseActionMistakeRepository.saveAll(List.of(mistake1,  mistake2, mistake3));

        // when
        List<String> result = phaseActionMistakeRepository.findContentByPhaseActionId(phase1.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("test-mistake-content-1", "test-mistake-content-2");
    }
}
