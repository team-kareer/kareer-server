package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.entity.phase.Phase;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseActionGuideline;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseActionMistake;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.domain.roadmap.repository.*;
import org.sopt.kareer.domain.roadmap.service.dto.response.AiGuideData;
import org.sopt.kareer.domain.roadmap.service.phaseaction.PhaseActionQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class PhaseActionQueryServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    @Autowired
    private PhaseActionMistakeRepository phaseActionMistakeRepository;

    @Autowired
    private PhaseActionGuidelineRepository phaseActionGuidelineRepository;

    @Autowired
    private PhaseActionQueryService phaseActionQueryService;

    private Phase phase1;
    private Member member1;
    private Roadmap roadmap1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
        roadmap1 = roadmapRepository.save(Roadmap.create(member1));
        phase1 = phaseRepository.save(PhaseFixture.getPhase(roadmap1, 1, PhaseStatus.CURRENT));
    }

    @AfterEach
    void tearDown() {
        phaseActionGuidelineRepository.deleteAll();
        phaseActionMistakeRepository.deleteAll();
        phaseActionRepository.deleteAllInBatch();
        phaseRepository.deleteAllInBatch();
        roadmapRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("AI 가이드를 조회한다.")
    class GetAiGuide {

        @Test
        @DisplayName("AI 가이드를 성공적으로 조회한다.")
        void getAiGuide_success() {
            // given
            PhaseAction phaseAction1 = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

            PhaseActionMistake mistake1 = PhaseActionMistake.create("test-content-1", phaseAction1);
            PhaseActionMistake mistake2 = PhaseActionMistake.create("test-content-2", phaseAction1);
            phaseActionMistakeRepository.saveAll(List.of(mistake1, mistake2));

            PhaseActionGuideline guideline1 = PhaseActionGuideline.create("test-content-1", phaseAction1);
            PhaseActionGuideline guideline2 = PhaseActionGuideline.create("test-content-2", phaseAction1);
            phaseActionGuidelineRepository.saveAll(List.of(guideline1, guideline2));

            // when
            AiGuideData data = phaseActionQueryService.getAiGuide(member1.getId(), phaseAction1.getId());

            // then
            assertThat(data).isNotNull();
            assertThat(data.importance()).isEqualTo("test-importance");
            assertThat(data.mistakes()).containsExactlyInAnyOrder("test-content-1", "test-content-2");
            assertThat(data.guidelines()).containsExactlyInAnyOrder("test-content-1", "test-content-2");
        }

        @Test
        @DisplayName("존재하지 않는 PhaseAction에 대한 AI 가이드를 조회할 경우 예외가 발생한다.")
        void getAiGuide_phaseActionNotFound() {
            // given
            Long phaseActionId = 0L;

            // when & then
            assertThatThrownBy(() -> phaseActionQueryService.getAiGuide(member1.getId(), phaseActionId))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 회원의 PhaseAction에 대한 AI 가이드를 조회할 경우 예외가 발생한다.")
        void getAiGuide_phaseActionNotFound_notOwner() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));
            PhaseAction phaseAction1 = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

            // when & then
            assertThatThrownBy(() -> phaseActionQueryService.getAiGuide(member2.getId(), phaseAction1.getId()))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND);
        }

        @Test
        @DisplayName("mistake와 guideline이 없어도 빈 리스트로 반환된다.")
        void getAiGuide_emptyLists() {
            // given
            PhaseAction phaseAction = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

            // when
            AiGuideData data = phaseActionQueryService.getAiGuide(member1.getId(), phaseAction.getId());

            // then
            assertThat(data.mistakes()).isEmpty();
            assertThat(data.guidelines()).isEmpty();
        }
    }
}
