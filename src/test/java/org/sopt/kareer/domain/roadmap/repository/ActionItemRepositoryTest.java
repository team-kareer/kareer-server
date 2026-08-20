package org.sopt.kareer.domain.roadmap.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.entity.phase.Phase;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class ActionItemRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    @Autowired
    private ActionItemRepository actionItemRepository;

    private Member member;
    private Roadmap roadmap;
    private PhaseAction phaseAction;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.getMember("action-item-repository-test"));
        roadmap = roadmapRepository.save(Roadmap.create(member));
        Phase phase = phaseRepository.save(PhaseFixture.getPhase(roadmap, 1, PhaseStatus.CURRENT));
        phaseAction = phaseActionRepository.save(
                PhaseActionFixture.getPhaseAction(phase, PhaseActionType.CAREER)
        );
    }

    @Test
    @DisplayName("로드맵 재생성 시 활성 ActionItem만 기존 PhaseAction에서 분리한다")
    void detachActiveItemsByRoadmapId() {
        // given
        ActionItem activeItem = ActionItem.create(
                "유지할 Todo",
                ActionItemType.CAREER,
                LocalDate.now().plusDays(1),
                member,
                phaseAction
        );
        activeItem.activate();

        ActionItem inactiveItem = ActionItem.create(
                "선택하지 않은 Todo",
                ActionItemType.CAREER,
                LocalDate.now().plusDays(2),
                member,
                phaseAction
        );
        actionItemRepository.save(activeItem);
        actionItemRepository.save(inactiveItem);

        // when
        actionItemRepository.detachActiveItemsByRoadmapId(roadmap.getId());
        actionItemRepository.flush();

        // then
        ActionItem detachedItem = actionItemRepository.findById(activeItem.getId()).orElseThrow();
        ActionItem stillLinkedItem = actionItemRepository.findById(inactiveItem.getId()).orElseThrow();

        assertThat(detachedItem.getPhaseAction()).isNull();
        assertThat(stillLinkedItem.getPhaseAction()).isNotNull();
    }
}
